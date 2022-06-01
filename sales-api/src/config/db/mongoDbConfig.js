import mongoose from 'mongoose'
import {MONGO_DB_URL} from '../secrets/secrets'


export function connectMongoDb() {
    mongoose.connect(MONGO_DB_URL, {
    })

    mongoose.connection.on("connected", function() {
        console.info("The application connected to MongoDB successfuly")
    })

    mongoose.connection.on("error", function () {
        console.error("The application connected to MongoDB result in error...")
    })
}