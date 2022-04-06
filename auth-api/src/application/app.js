import express from "express";
import dotenv from 'dotenv'
dotenv.config()


import * as db from '../infra/sequelize/initialData.js'

import userRoutes from '../modules/user/routes/UserRoutes'

const app = express()

app.use(express.json())

app.use(userRoutes)


db.createInitialData()

const env = process.env

const PORT = env.PORT || 8080

app.use('/api/status', (req, res) => {
    return res.status(200).json({
        service: 'Auth-API',
        status: 'up',
        httpStatus: 200
    })
})

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`)
})