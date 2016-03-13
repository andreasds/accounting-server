package com.andreas.accounting.administrator.saldoawal

import grails.converters.JSON

class PiutangAwalController {

    def piutangAwalService

    def index() {
        render piutangAwalService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'jumlah'
        params.order = params.order != 'null' ? params.order : 'asc'
        render piutangAwalService.list(params) as JSON
    }

    def save() {
        render piutangAwalService.save(request.JSON) as JSON
    }

    def get(long id) {
        render piutangAwalService.get(id) as JSON
    }

    def update(long id) {
        render piutangAwalService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render piutangAwalService.delete(id) as JSON
    }

    def checkNo(String no, long perusahaanId) {
        render piutangAwalService.checkNo(no, perusahaanId)
    }

    def getTotal(long perusahaanId) {
        render piutangAwalService.getTotal(perusahaanId)
    }

    def count() {
        render piutangAwalService.count(params)
    }
}
