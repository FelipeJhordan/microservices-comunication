import "dotenv/config"

const env = process.env

export const apiSecret = env.API_SECRET ? env.API_SECRET: "JDK"