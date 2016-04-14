package com.andreas.accounting.penjualan

import grails.converters.JSON

class InvoicePenjualanController {

    def invoicePenjualanService

    def index() {
        render invoicePenjualanService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'total'
        params.order = params.order != 'null' ? params.order : 'desc'
        render invoicePenjualanService.list(params, request.JSON) as JSON
    }

    def save() {
        render invoicePenjualanService.save(request.JSON) as JSON
    }

    def get(long id) {
        render invoicePenjualanService.get(id) as JSON
    }

    def update(long id) {
        render invoicePenjualanService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render invoicePenjualanService.delete(id) as JSON
    }

    def checkNo(String no, long pemilikId) {
        render invoicePenjualanService.checkNo(no, pemilikId)
    }

    def count() {
        render invoicePenjualanService.count(params, request.JSON)
    }
}
