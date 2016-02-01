import com.andreas.accounting.auth.*
import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
        switch(Environment.current) {
        case Environment.DEVELOPMENT:
            def admin = new User('admin', 'admin').save(failOnError: true)
            def roleAdmin = new Role('ROLE_ADMIN').save(failOnError: true)
            def roleUser = new Role('ROLE_USER').save(failOnError: true)
            UserRole.create(admin, roleAdmin, true)
            UserRole.create(admin, roleUser, true)
            break
        default:
            break
        }
    }
    
    def destroy = {
        
    }
}
