import com.andreas.accounting.auth.*
import grails.util.Environment

class BootStrap {

    def administratorService
    
    def init = { servletContext ->
        switch(Environment.current) {
        case Environment.DEVELOPMENT:
            administratorService.init();
            break
        default:
            break
        }
    }
    
    def destroy = {
        
    }
}
