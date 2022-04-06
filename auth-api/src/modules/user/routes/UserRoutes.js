import { Router } from 'express'
import checkToken from '../../../application/middlewares/auth/checkToken'

import UserController from '../controller/UserController'

const router = new Router()

router.post('/api/user/auth', UserController.getAccessToken)

router.use(checkToken)

router.get('/api/user/email/:email', UserController.findByEmail)

export default router