package com.andreas.accounting.administrator.daftarnama

import grails.converters.JSON

class PerusahaanController {
    
    def perusahaanService

    def index() {
        render perusahaanService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'nama'
        params.order = params.order != 'null' ? params.order : 'asc'
        render perusahaanService.list(params) as JSON
    }

    def listNama() {
        render perusahaanService.listNama() as JSON
    }

    def listNamaPemilik() {
        render perusahaanService.listNamaPemilik() as JSON
    }
    
    def save() {
        render perusahaanService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render perusahaanService.get(id) as JSON
    }
    
    def update(long id) {
        render perusahaanService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render perusahaanService.delete(id) as JSON
    }
    
    def checkNama(String nama) {
        render perusahaanService.checkNama(nama)
    }
    
    def count() {
        render perusahaanService.count(params)
    }
}
