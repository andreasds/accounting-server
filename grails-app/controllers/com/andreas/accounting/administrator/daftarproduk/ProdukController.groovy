package com.andreas.accounting.administrator.daftarproduk

import grails.converters.JSON

class ProdukController {

    def produkService

    def index() {
        render produkService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'kategoriProduk.kode'
        params.order = params.order != 'null' ? params.order : 'asc'
        render produkService.list(params, request.JSON) as JSON
    }

    def listKode() {
        render produkService.listKode() as JSON
    }

    def save() {
        render produkService.save(request.JSON) as JSON
    }

    def get(long id) {
        render produkService.get(id) as JSON
    }

    def update(long id) {
        render produkService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render produkService.delete(id) as JSON
    }

    def count() {
        render produkService.count(params, request.JSON)
    }
}
