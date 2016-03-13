package com.andreas.accounting.administrator.saldoawal

import grails.converters.JSON

class HutangAwalController {
    
    def hutangAwalService

    def index() {
        render hutangAwalService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'jumlah'
        params.order = params.order != 'null' ? params.order : 'asc'
        render hutangAwalService.list(params) as JSON
    }
    
    def save() {
        render hutangAwalService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render hutangAwalService.get(id) as JSON
    }
    
    def update(long id) {
        render hutangAwalService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render hutangAwalService.delete(id) as JSON
    }
    
    def checkNo(String no, long perusahaanId) {
        render hutangAwalService.checkNo(no, perusahaanId)
    }
    
    def getTotal(long perusahaanId) {
        render hutangAwalService.getTotal(perusahaanId)
    }
    
    def count() {
        render hutangAwalService.count(params)
    }
}
