import grails.util.Environment

class BootStrap {

    def administratorService
    def mataUangService
    
    def init = { servletContext ->
        switch(Environment.current) {
        case Environment.DEVELOPMENT:
            administratorService.init()
            mataUangService.init()
            break
        case Environment.TEST:
            administratorService.init()
            mataUangService.init()
            break
        default:
            break
        }
    }
    
    def destroy = {
        
    }
}
