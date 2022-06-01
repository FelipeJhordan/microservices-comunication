import amqp from 'amqplib/callback_api'

import { SALES_CONFIRMATION_QUEUE } from '../../../config/rabbitmq/queue'
import { RABBIT_MQ_URL } from '../../../config/secrets/secrets'
import OrderService from '../service/OrderService'


export function listenToSalesConfirmationQueue() {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if(error) throw error

        console.info("Listening to sales confirmation Queue listen...")
        connection.createChannel((error,channel) => {
            if(error) {
                throw error
            }
            channel.consume(SALES_CONFIRMATION_QUEUE, (message) => {
                console.info(`Recieve message from queue: ${message.content.toString()}`)
                OrderService.updateOrder(message.content.toString())
            }, {
                noAck: true
            })
        })
        
    })
}