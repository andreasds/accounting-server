package com.andreas.accounting.administrator

import grails.converters.JSON

class RekeningController {

    def rekeningService

    def index() {
        render rekeningService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'nama'
        params.order = params.order != 'null' ? params.order : 'asc'
        render rekeningService.list(params) as JSON
    }

    def listNama() {
        render rekeningService.listNama() as JSON
    }
    
    def save() {
        render rekeningService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render rekeningService.get(id) as JSON
    }
    
    def update(long id) {
        render rekeningService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render rekeningService.delete(id) as JSON
    }
    
    def checkNama(String nama) {
        render rekeningService.checkNama(nama)
    }
    
    def count() {
        render rekeningService.count(params)
    }
}
