import Sequelize from 'sequelize'

const sequelize = new Sequelize("postgres", "admin", "123456", {
    host: 'localhost',
    port: '5432',
    dialect: 'postgres',
    quoteIdentifiers: false,
    logging: false,
    define: {
        syncOnAssociation: true,
        timestamps: false,
        underscored: true,
        underscoredAll: true,
        freezeTableName: true,

    },

})


sequelize
    .authenticate()
    .then(() => {
        console.log("Connection has been stablished")
    })
    .catch((e) => {
        console.error("Unable connect to the database.")
        console.log(e.message)
    })

export default sequelize