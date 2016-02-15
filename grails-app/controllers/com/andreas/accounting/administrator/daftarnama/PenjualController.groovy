package com.andreas.accounting.administrator.daftarnama

import grails.converters.JSON

class PenjualController {
    
    def penjualService

    def index() {
        render penjualService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'nama'
        params.order = params.order != 'null' ? params.order : 'asc'
        render penjualService.list(params) as JSON
    }
    
    def listNama() {
        render penjualService.listNama() as JSON
    }
    
    def save() {
        render penjualService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render penjualService.get(id) as JSON
    }
    
    def update(long id) {
        render penjualService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render penjualService.delete(id) as JSON
    }
    
    def count() {
        render penjualService.count(params)
    }
}