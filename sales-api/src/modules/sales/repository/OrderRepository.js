import Order from '../model/Order'

class OrderRepository {
    async save(order) {
        try {
            return await Order.create(order)
        } catch(e) {
            console.error(e.message)
            return null
        }
    }

    async findById(id) {
        try {
            return await Order.findById(id)
        } catch(e) {
            console.error(e.message)
            return null
        }
    }

    async findAll() {
        try {
            return await Order.find()
        } catch(e) {
            console.error(e.message)
            return null
        }
    }

    async findByProductId(productId) {
        try {
            return await Order.find({"products.productId": Number(productId)})
        } catch(e) {
           console.error(e.message) 
           return null
        }
    }
}

export default new OrderRepository()