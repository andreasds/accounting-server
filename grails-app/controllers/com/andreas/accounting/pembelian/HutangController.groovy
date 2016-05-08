package com.andreas.accounting.pembelian

import grails.converters.JSON

class HutangController {

    def hutangService

    def index() {
        render hutangService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.order = params.order != 'null' ? params.order : 'desc'

        if (request.JSON['penjualId'] != 0) {
            params.sort = params.sort != 'null' ? params.sort : 'invoice.tanggal'
            render hutangService.listByOrang(params, request.JSON) as JSON
        } else {
            params.sort = params.sort != 'null' ? params.sort : 'sisa'
            render hutangService.list(params, request.JSON) as JSON
        }
    }

    def listBayar() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'tanggal'
        params.order = params.order != 'null' ? params.order : 'asc'
        render hutangService.listBayar(params, request.JSON) as JSON
    }

    def save() {
        render hutangService.save(request.JSON) as JSON
    }

    def get(long id) {
        render hutangService.get(id) as JSON
    }

    def update(long id) {
        render hutangService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render hutangService.delete(id) as JSON
    }

    def getTotal(long penjualId, long pemilikId) {
        if (penjualId != 0) {
            render hutangService.getTotalByOrang(penjualId, pemilikId)
        } else {
            render hutangService.getTotal(pemilikId)
        }
    }

    def count() {
        if (request.JSON['penjualId'] != 0) {
            render hutangService.countByOrang(params, request.JSON)
        } else {
            render hutangService.count(params, request.JSON)
        }
    }

    def countBayar() {
        render hutangService.countBayar(params, request.JSON)
    }
}
