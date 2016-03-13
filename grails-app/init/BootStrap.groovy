import grails.util.Environment

class BootStrap {

    def administratorService
    def mataUangService
    
    def init = { servletContext ->
        administratorService.init()
        mataUangService.init()
        
        switch(Environment.current) {
        case Environment.DEVELOPMENT:
            break
        case Environment.TEST:
            break
        case Environment.PRODUCTION:
            break
        default:
            break
        }
    }
    
    def destroy = {
        
    }
}
