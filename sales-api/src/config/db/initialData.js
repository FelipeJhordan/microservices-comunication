import Order from "../../modules/sales/model/Order";

export async function createInitialData() {
    await Order.collection.drop()
    await Order.create({
        products: [
            {
                productId: 1,
                quantity: 2
            },
            {
                productId: 3,
                quantity: 1
            },
        ],
        user: {
            id: "asdasadsakdsak",
            name: "User teste",
            email: "userteste@gmail.com"
        },
        status: "APPROVED",
        updatedAt: new Date(),
        createdAt: new Date()
    })
    await Order.create({
        products: [
            {
                productId: 1,
                quantity: 2
            },
            {
                productId: 2,
                quantity: 1
            },
            {
                productId: 3,
                quantity: 2
            },
        ],
        user: {
            id: "fsadsadsa",
            name: "User teste2",
            email: "userteste2@gmail.com"
        },
        status: "REJECTED",
        updatedAt: new Date(),
        createdAt: new Date()
    })
}