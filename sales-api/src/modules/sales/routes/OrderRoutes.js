import { Router } from 'express'

import OrderController from '../controller/OrderController'

const router = new Router()

router.post("/api/order/create", OrderController.createOrder)
router.get("/api/order/:id", OrderController.findById)
router.get("/api/order", OrderController.findAll)
router.get("/api/order/product/:productId", OrderController.findByProductId)

export default router 