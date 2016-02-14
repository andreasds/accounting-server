package com.andreas.accounting.administrator.daftarproduk

import grails.converters.JSON

class SatuanController {
    
    def satuanService

    def index() {
        render satuanService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'kode'
        params.order = params.order != 'null' ? params.order : 'asc'
        render satuanService.list(params) as JSON
    }

    def listKode() {
        render satuanService.listKode() as JSON
    }
    
    def save() {
        render satuanService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render satuanService.get(id) as JSON
    }
    
    def update(long id) {
        render satuanService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render satuanService.delete(id) as JSON
    }
    
    def count() {
        render satuanService.count(params)
    }
}
