package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.Rekening
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class RekeningAwalService {

    def listAll() {
        return RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
                order('nama', 'asc')
            }
            rekening {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            order('saldo', 'asc')
            projections {
                property('id', 'id')
                property('saldo', 'saldo')
                property('perusahaan', 'perusahaan')
                property('rekening', 'rekening')
            }
        }
    }

    def list(params, data) {
        def rekeningAwals = RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (data.containsKey('perusahaan.id')) {
                    idEq(data['perusahaan.id'])
                }
                eq('activeStatus', 'Y')
            }
            rekening {
                if (data.containsKey('rekening.nama')) {
                    ilike('nama', "%${data['rekening.nama']}%")
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'rekening.nama') {
                    order('nama', params.order)
                }
            }

            if (data.containsKey('saldo')) {
                le('saldo', new BigDecimal(data['saldo']))
            }

            eq('activeStatus', 'Y')
            if (params.sort == 'saldo') {
                order(params.sort, params.order)
            }
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('saldo', 'saldo')
                property('perusahaan', 'perusahaan')
                property('rekening', 'rekening')
            }
        }

        if (!rekeningAwals.empty) {
            rekeningAwals.each { rekeningAwal ->
                def perusahaan = [:]
                perusahaan['id'] = rekeningAwal['perusahaan']['id']
                perusahaan['nama'] = rekeningAwal['perusahaan']['nama']
                rekeningAwal['perusahaan'] = perusahaan

                def rekening = [:]
                rekening['id'] = rekeningAwal['rekening']['id']
                rekening['nama'] = rekeningAwal['rekening']['nama']
                rekeningAwal['rekening'] = rekening
            }
        }
        return rekeningAwals
    }

    def save(data) {
        def rekeningAwal = new RekeningAwal()
        rekeningAwal.saldo = data.saldo
        rekeningAwal.activeStatus = 'Y'
        rekeningAwal.perusahaan = Perusahaan.get(data.perusahaan.id)
        rekeningAwal.rekening = Rekening.get(data.rekening.id)

        def response = [:]
        if (rekeningAwal.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = rekeningAwal.id
        } else {
            response['message'] = 'failed'
            response['error'] = rekeningAwal.errors.allErrors.code
        }
        return response
    }

    def get(id) {
        def rekeningAwal = RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
                order('nama', 'asc')
            }
            rekening {
                eq('activeStatus', 'Y')
            }
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('saldo', 'saldo')
                property('perusahaan', 'perusahaan')
                property('rekening', 'rekening')
            }
        }

        if (!rekeningAwal.empty) {
            rekeningAwal = rekeningAwal[0]

            def perusahaan = [:]
            perusahaan['id'] = rekeningAwal['perusahaan']['id']
            perusahaan['nama'] = rekeningAwal['perusahaan']['nama']
            rekeningAwal['perusahaan'] = perusahaan

            def rekening = [:]
            rekening['id'] = rekeningAwal['rekening']['id']
            rekening['nama'] = rekeningAwal['rekening']['nama']
            rekeningAwal['rekening'] = rekening
        }
        return rekeningAwal
    }

    def update(id, data) {
        def rekeningAwal = RekeningAwal.get(id)
        rekeningAwal.saldo = data.saldo
        rekeningAwal.perusahaan = Perusahaan.get(data.perusahaan.id)
        rekeningAwal.rekening = Rekening.get(data.rekening.id)

        def response = [:]
        if (rekeningAwal.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = rekeningAwal.id
        } else {
            response['message'] = 'failed'
            response['error'] = rekeningAwal.errors.allErrors.code
        }
        return response
    }

    def delete(id) {
        def rekeningAwal = RekeningAwal.get(id)
        rekeningAwal.activeStatus = 'N'

        def response = [:]
        if (rekeningAwal.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }

    def checkRekening(rekeningId, perusahaanId) {
        return RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                idEq(perusahaanId)
            }
            rekening {
                idEq(rekeningId)
            }
        }.size()
    }

    def getTotal(perusahaanId) {
        def total = RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (perusahaanId != 0) {
                    idEq(perusahaanId)
                }
                eq('activeStatus', 'Y')
            }
            rekening {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                sum('saldo', 'saldo')
            }
        }[0]['saldo']
        return total != null ? total : 0
    }

    def count(params, data) {
        return RekeningAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (data.containsKey('perusahaan.id')) {
                    idEq(data['perusahaan.id'])
                }
                eq('activeStatus', 'Y')
            }
            rekening {
                if (data.containsKey('rekening.nama')) {
                    ilike('nama', "%${data['rekening.nama']}%")
                }
                eq('activeStatus', 'Y')
            }
            if (data.containsKey('saldo')) {
                le('saldo', new BigDecimal(data['saldo']))
            }
            eq('activeStatus', 'Y')
        }.size()
    }
}
