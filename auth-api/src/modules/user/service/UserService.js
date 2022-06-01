import bcrypt from 'bcrypt'
import jwt from 'jsonwebtoken'

import UserRepository from "../repository/UserRepository"
import * as httpStatus from '../../../application/constants/httpStatus'
import UserException from "../exception/UserException"
import { apiSecret } from '../../../application/constants/jwtSecret'


class UserService {
    
    async findByEmail(req, res) {
        try {
            const { email } = req.params 
            const { authUser } = req.params
            this.validateRequestData(email)
            let user = await UserRepository.findByEmail(email)
            this.validateUserNotFound(user)
            this.validateAuthenticatedUser(user, authUser)

            return {
                status: httpStatus.SUCCESS,
                user: {
                    id: user.id,
                    name: user.name,
                    email: user.email
                }
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }

    async getAccessToken(req) {
        try {
            const {email, password} = req.body

            this.validateAccessTokenData(email, password)

            let user = await UserRepository.findByEmail(email)

            this.validateUserNotFound(user)

            await this.validatePassword(password, user.password)

            const authUser = { id: user.id, name: user.name, email: user.email}
            const accessToken = jwt.sign({authUser}, apiSecret, { expiresIn: '1d'})

            return {
                status: httpStatus.SUCCESS,
                accessToken
            }
        } catch(e) {
            return {
                status: e.status ? e.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: e.message
            }
        }
    }


    validateAccessTokenData(email, password) {
        if(!email || !password) {
            throw new UserException(httpStatus.UNAUTHORIZED, 'Email and password must be informed')
        }
    }

    validateRequestData(email) {
        if(!email) {
            throw new UserException(httpStatus.BAD_REQUEST, "User email was not informed.")
        }
    }

    validateUserNotFound(user) {
        if(!user) {
            throw new UserException(httpStatus.NOT_FOUND, "User not found.")
        }
    }

    validateAuthenticatedUser(user, authUser) {
        if(!authUser || user.id !== authUser.id) {
            throw new UserException(httpStatus.FORBIDDEN, 'You cannot see this data.')
        }
    }

    async validatePassword(password, hashPassword) {
        if(!await bcrypt.compare(password, hashPassword)) {
            throw new UserException(httpStatus.UNAUTHORIZED, "Password doesn´t match.")
        }
    }
}

export default new UserService()