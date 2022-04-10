import express from 'express'

import { connectMongoDb } from '../config/db/mongoDbConfig'

import { createInitialData } from '../config/db/initialData'
import { connectRabbitMq } from '../config/rabbitmq/rabbitConfig'
import Order from '../modules/sales/model/Order'
import checkToken from '../config/middlewares/auth/checkToken'
import orderRoutes from '../modules/sales/routes/OrderRoutes'

import { sendProductStockUpdateQueue } from '../modules/product/rabbitmq/productStockUpdateSender'

const app = express()

const env = process.env

const PORT = env.PORT || 8082

app.use(express.json())

connectMongoDb()
createInitialData()
connectRabbitMq()

app.use(checkToken)

app.use(orderRoutes)

app.get('/api/status', async (req, res) => {
    console.log(await Order.find())
    return res.status(200).json({
        service: 'Sales-API',
        status: 'up',
        httpStatus: 200
    })
})

app.get("/teste", async(req, res) => {
    try {
        sendProductStockUpdateQueue([
            {
                productId: 1,
                quantity: 1
            }
        ])
        return res.status(200).json({status:200})
    } catch(e) {
        console.log(e)
        return res.status(500).json({error:true})
    }
})

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`)
})