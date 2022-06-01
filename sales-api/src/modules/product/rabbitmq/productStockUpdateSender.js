import amqp from 'amqplib/callback_api'

import { PRODUCT_TOPIC, PRODUCT_STOCK_UPDATE_ROUTING_KEY } from '../../../config/rabbitmq/queue'
import { RABBIT_MQ_URL } from '../../../config/secrets/secrets'

export function sendProductStockUpdateQueue(message) {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if(error) throw error

        
        connection.createChannel((error,channel) => {
            let jsonStringMessage = JSON.stringify(message)
            console.log(jsonStringMessage)
            if(error) {
                throw error
            }
            console.log("Sending message to product update stock: " + jsonStringMessage)
            channel.publish(PRODUCT_TOPIC,PRODUCT_STOCK_UPDATE_ROUTING_KEY, Buffer.from(jsonStringMessage))
        })
        console.info("Message was sent successfully!")
    })
}