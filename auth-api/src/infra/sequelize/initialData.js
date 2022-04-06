import bcrypt from 'bcrypt'
import User from '../../modules/user/model/User'

export async function createInitialData() {
    try {
        await User.sync({
            force: true
        })
        let password = await bcrypt.hash('123456', 10)
    
       await Promise.all(
            [
                User.create(
                    {
                        name: 'User Test 1',
                        email: 'test1e@gmail.com',
                        password: password
                    }
                ),
                 User.create(
                    {
                        name: 'User Test 2',
                        email: 'teste2@gmail.com',
                        password: password
                    }
                )
            ]
        )
    } catch(e) {
        console.error(e)
    }
}