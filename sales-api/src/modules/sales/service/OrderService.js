import OrderRepository from "../repository/OrderRepository";
import { sendProductStockUpdateQueue } from "/users/felip/desktop/projetos-portifolio/microservices-comunication/sales-api/src/modules/product/rabbitmq/productstockupdatesender";
import * as httpStatus from '../../../config/constants/httpStatus'
import { PENDING } from "../status/OrderStatus";
import OrderException from "../exception/SaleException";
import ProductClient from "../../product/client/ProductClient";

class OrderService {
    async createOrder(req) {
        try {
            let orderData = req.body
            const { authUser } = req
            const { authorization } = req.headers
            this.validateOrderData(orderData)
            let order = await this.createInitialOrder(orderData, authUser)
            let createdOrder = await OrderRepository.save(order)
            await this.sendMessage(createdOrder)
            await this.validateProductStock(order, authorization)   
            return {
                status: httpStatus.SUCCESS,
                createdOrder
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }

    createInitialOrder(orderData, authUser) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            products: orderData.products
        }
    }

    async updateOrder(orderMessage) {
        try {
            const order = JSON.parse(orderMessage)
            let existingOrder = await OrderRepository.findById(order.salesId)
            if(order.salesId && order.status ) {
                if(existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status
                    existingOrder.updatedAt = new Date()
                    await OrderRepository.save(existingOrder)
                }
            } else {
                console.warn("The order message was not complete.")
            }
        } catch(err) {
            console.error("Could not parse order message from queue.")
            console.error(err.message)
        }
    }

    async sendMessage(createdOrder) {
        console.log("aqui "+createdOrder)
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products
        }

        sendProductStockUpdateQueue(message)
    }

    async findById(req) {
        
        try {

            const { id }  = req.params
            this.validateInformedId(id)
            const existingOrder = await OrderRepository.findById(id)
            if(!existingOrder) {
                throw new OrderException(httpStatus.BAD_REQUEST, "The order was not found")
            }
    
            return {
                status: httpStatus.SUCCESS,
                existingOrder
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }

    async findAll() {
        
        try {

            const orders  = await OrderRepository.findAll()
            if(!orders) {
                throw new OrderException(httpStatus.NOT_FOUND, "No orders were found")
            }
    
            return {
                status: httpStatus.SUCCESS,
                orders
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }

    async findByProductId(req) {
        
        try {
            const { productId }  = req.params
            this.validateProductId(productId)
            const orders  = await OrderRepository.findByProductId(productId)
            if(!orders) {
                throw new OrderException(httpStatus.NOT_FOUND, "No orders were found")
            }
    
            return {
                status: httpStatus.SUCCESS,
                salesIds: orders.map((order) => order.id)
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }

    validateOrderData(data) {
        if(!data  || !data.products) {
            throw new OrderException(httpStatus.BAD_REQUEST, "The products must be informed.")
        }
    }

    async validateProductStock (order, token) {
        let stockIsOut = await ProductClient.checkProductStock(order, token)
        if(stockIsOut) {
            throw new OrderException(httpStatus.BAD_REQUEST, "The stock is out for the productS")
        }
    }


    validateInformedId(id) {
        if(!id) {
            throw new OrderException(httpStatus.BAD_REQUEST, "tHE ORDER id MUST BE informed")
        }
    }

    validateProductId(id) {
        if(!id) {
            throw new OrderException(httpStatus.BAD_REQUEST, "tHE PRODUCT id MUST BE informed")
        }
    }
}

export default new OrderService()