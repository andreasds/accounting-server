package com.andreas.accounting.util

import grails.converters.JSON

class MenuController {
    
    def menuService
    
    def index() {
        render menuService.authorizedMenu() as JSON
    }
}
