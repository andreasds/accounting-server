package com.andreas.accounting.util

import grails.converters.JSON

class MataUangController {
    
    def mataUangService

    def index() {
        render mataUangService.listAll() as JSON
    }
    
    def listKode() {
        render mataUangService.listAll() as JSON
    }
    
    def get(long id) {
        render mataUangService.get(id) as JSON
    }
    
    def getIDR() {
        render mataUangService.getIDR() as JSON
    }
}
