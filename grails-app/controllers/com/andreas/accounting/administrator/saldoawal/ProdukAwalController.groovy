package com.andreas.accounting.administrator.saldoawal

import grails.converters.JSON

class ProdukAwalController {

    def produkAwalService

    def index() {
        render produkAwalService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'jumlah'
        params.order = params.order != 'null' ? params.order : 'asc'
        render produkAwalService.list(params, request.JSON) as JSON
    }

    def save() {
        render produkAwalService.save(request.JSON) as JSON
    }

    def get(long id) {
        render produkAwalService.get(id) as JSON
    }

    def update(long id) {
        render produkAwalService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render produkAwalService.delete(id) as JSON
    }

    def checkProduk(long produkId, long perusahaanId) {
        render produkAwalService.checkProduk(produkId, perusahaanId)
    }

    def getTotal(long perusahaanId) {
        render produkAwalService.getTotal(perusahaanId)
    }

    def count() {
        render produkAwalService.count(params, request.JSON)
    }
}
