import axios from 'axios'

import {
    PRODUCT_API_URL
} from '../../../config/secrets/secrets'


class ProductClient {
    async checkProductStock(productsData, token) {
        try {
            const headers = {
                Authorization: token
            }
            let response = false;
            console.info("Sending request to Product API with data", JSON.stringify(productsData))
            await axios.post(`${PRODUCT_API_URL}/check-stock`, {products: productsData}, {headers})
            .then( res => {
                response = true
            })
            .catch((err) => {
                response = false
            })
            return response
        } catch(err) {
            return false
        }
    }
}

export default new ProductClient()