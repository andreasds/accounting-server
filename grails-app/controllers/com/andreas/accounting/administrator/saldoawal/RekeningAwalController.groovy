package com.andreas.accounting.administrator.saldoawal

import grails.converters.JSON

class RekeningAwalController {

    def rekeningAwalService

    def index() {
        render rekeningAwalService.listAll() as JSON
    }

    def list() {
        params.offset = params.offset != 'null' ? params.int('offset') : 0
        params.max = params.max != 'null' ? params.int('max') : 10
        params.sort = params.sort != 'null' ? params.sort : 'saldo'
        params.order = params.order != 'null' ? params.order : 'asc'
        render rekeningAwalService.list(params) as JSON
    }

    def save() {
        render rekeningAwalService.save(request.JSON) as JSON
    }

    def get(long id) {
        render rekeningAwalService.get(id) as JSON
    }

    def update(long id) {
        render rekeningAwalService.update(id, request.JSON) as JSON
    }

    def delete(long id) {
        render rekeningAwalService.delete(id) as JSON
    }

    def checkRekening(long rekeningId, long perusahaanId) {
        render rekeningAwalService.checkRekening(rekeningId, perusahaanId)
    }

    def getTotal(long perusahaanId) {
        render rekeningAwalService.getTotal(perusahaanId)
    }

    def count() {
        render rekeningAwalService.count(params)
    }
}
