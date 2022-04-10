import amqp from 'amqplib/callback_api'
import {listenToSalesConfirmationQueue} from '../../modules/sales/rabbitmq/salesConfirmationListener'

import {
    PRODUCT_STOCK_UPDATE_QUEUE,
    PRODUCT_STOCK_UPDATE_ROUTING_KEY,
    PRODUCT_TOPIC,
    SALES_CONFIRMATION_QUEUE,
    SALES_CONFIRMATION_ROUTING_KEY}  from './queue'

import { RABBIT_MQ_URL } from '../secrets/secrets'

const TWO_SECONDS = 2000
const HALF_MINUTE = 30000
const CONTAINER_ENV = "container"

function connectRabbitMqAndCreateQueues() {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if(error) {
            throw error
        }
        createQueue(connection, 
            PRODUCT_STOCK_UPDATE_QUEUE,
            PRODUCT_STOCK_UPDATE_ROUTING_KEY,
            PRODUCT_TOPIC)
        createQueue(connection, 
            SALES_CONFIRMATION_QUEUE,
            SALES_CONFIRMATION_ROUTING_KEY,
            PRODUCT_TOPIC)
        setTimeout(function() {
            connection.close()
        }, HALF_MINUTE)
    })
    setTimeout(() => listenToSalesConfirmationQueue(), 2000)
}

export async function connectRabbitMq() {
    const env = await process.env.NODE_ENV;
    if(CONTAINER_ENV === env.trim()) {
        console.log("Waiting for RabbitMQ to start...")

        setInterval(async function() {
            await connectRabbitMqAndCreateQueues()
        }, HALF_MINUTE)
        console.log("aqui")
        return
    }
    await connectRabbitMqAndCreateQueues()
}


function createQueue(connection, queue, routingKey, topic) {
    connection.createChannel((error, channel) => {
        channel.assertExchange(topic, "topic", { durable: true})
        channel.assertQueue(queue, { durable: true})
        channel.bindQueue( queue, topic, routingKey)
    })
}