package com.andreas.accounting.administrator.saldoawal

import grails.converters.JSON

class AwalPersediaanController {
    
    def awalPersediaanService

    def index() {
        render awalPersediaanService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'indeks'
        params.order = params.order != 'null' ? params.order : 'asc'
        render awalPersediaanService.list(params) as JSON
    }
    
    def save(long id) {
        render awalPersediaanService.update(id, request.JSON) as JSON
    }
    
    def get(long id) {
        render awalPersediaanService.get(id) as JSON
    }
    
    def update(long id) {
        render awalPersediaanService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render awalPersediaanService.delete(id) as JSON
    }
    
    def checkProduk(long id) {
        render awalPersediaanService.checkProduk(id)
    }
    
    def count() {
        render awalPersediaanService.count(params)
    }
}
