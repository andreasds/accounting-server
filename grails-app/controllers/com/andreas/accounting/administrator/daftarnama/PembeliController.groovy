package com.andreas.accounting.administrator.daftarnama

import grails.converters.JSON

class PembeliController {
    
    def pembeliService

    def index() {
        render pembeliService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'nama'
        params.order = params.order != 'null' ? params.order : 'asc'
        render pembeliService.list(params) as JSON
    }
    
    def listNama() {
        render pembeliService.listNama() as JSON
    }
    
    def save() {
        render pembeliService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render pembeliService.get(id) as JSON
    }
    
    def update(long id) {
        render pembeliService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render pembeliService.delete(id) as JSON
    }
    
    def checkNama(String nama, long perusahaanId) {
        render pembeliService.checkNama(nama, perusahaanId)
    }
    
    def count() {
        render pembeliService.count(params)
    }
}
