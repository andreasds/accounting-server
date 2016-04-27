package com.andreas.accounting.pembelian

import grails.converters.JSON

class InvoicePembelianController {

    def invoicePembelianService

    def index() {
        render invoicePembelianService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'tanggal'
        params.order = params.order != 'null' ? params.order : 'desc'
        render invoicePembelianService.list(params, request.JSON) as JSON
    }

    def save() {
        render invoicePembelianService.save(request.JSON) as JSON
    }

    def get(long id) {
        render invoicePembelianService.get(id) as JSON
    }

    def update(long id) {
        render invoicePembelianService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render invoicePembelianService.delete(id) as JSON
    }

    def checkNo(String no, long pemilikId) {
        render invoicePembelianService.checkNo(no, pemilikId)
    }

    def count() {
        render invoicePembelianService.count(params, request.JSON)
    }
}
