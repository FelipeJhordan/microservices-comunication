import User from '../model/User'


class UserRepository {

    async findById(id) {
        try {
            return await User.findOne({ where: { id }})
        } catch(e) {
            console.log(e.message)
            return null
        }
    }

    async findByEmail(email) {
        try {
            return await User.findOne({ where: {
                email
            }})
        } catch(e) {
            console.log(e.message)
            return null
        }
    }
}

export default new UserRepository()