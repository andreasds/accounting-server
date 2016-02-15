package com.andreas.accounting.administrator.daftarproduk

import grails.converters.JSON

class KategoriProdukController {
    
    def kategoriProdukService

    def index() {
        render kategoriProdukService.listAll() as JSON
    }
    
    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'nama'
        params.order = params.order != 'null' ? params.order : 'asc'
        render kategoriProdukService.list(params) as JSON
    }

    def listNama() {
        render kategoriProdukService.listNama() as JSON
    }
    
    def save() {
        render kategoriProdukService.save(request.JSON) as JSON
    }
    
    def get(long id) {
        render kategoriProdukService.get(id) as JSON
    }
    
    def update(long id) {
        render kategoriProdukService.update(id, request.JSON) as JSON
    }
    
    def delete(long id) {
        render kategoriProdukService.delete(id) as JSON
    }
    
    def count() {
        render kategoriProdukService.count(params)
    }
}
