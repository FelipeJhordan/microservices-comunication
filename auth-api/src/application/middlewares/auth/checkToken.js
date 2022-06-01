import jwt from 'jsonwebtoken'
import { promisify } from 'util'
import AuthException from './AuthException'
import * as httpStatus from '../../../application/constants/httpStatus'
import { apiSecret } from '../../constants/jwtSecret'


const bearer = 'Bearer '

export default async (req, res, next) => {
    try {
    const { authorization } = req.headers

    if(!authorization) {
        throw new AuthException(httpStatus.UNAUTHORIZED, "Access token was not informed")
    }

    let accessToken = authorization
    if( accessToken.includes(bearer)) {
        accessToken = accessToken.replace(bearer, "")
    }
    const decoded = await promisify(jwt.verify)(
        accessToken,
        apiSecret
    )

    req.authUser = decoded.authUser

    return next()
    } catch(e) {
        return res.status(400).send( {
            status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
            message: e.message
        })
    }
}