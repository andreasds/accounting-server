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
        params.sort = params.sort != 'null' ? params.sort : 'total'
        params.order = params.order != 'null' ? params.order : 'desc'

        if (request.JSON['perusahaanId'] != 0) {
            render piutangAwalService.listByPerusahaan(params, request.JSON) as JSON
        } else {
            render piutangAwalService.list(params, request.JSON) as JSON
        }
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

    def getTotal(long perusahaanId, long pemilikId) {
        if (perusahaanId != 0) {
            render piutangAwalService.getTotalByPerusahaan(perusahaanId, pemilikId)
        } else {
            render piutangAwalService.getTotal(pemilikId)
        }
    }

    def count() {
        if (request.JSON['perusahaanId'] != 0) {
            render piutangAwalService.countByPerusahaan(params, request.JSON)
        } else {
            render piutangAwalService.count(params, request.JSON)
        }
    }
}
