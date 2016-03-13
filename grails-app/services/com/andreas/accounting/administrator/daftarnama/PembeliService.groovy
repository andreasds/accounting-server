package com.andreas.accounting.administrator.daftarnama

import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.daftarnama.Perusahaan
import grails.converters.JSON
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class PembeliService {

    def listAll() {
        return Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            eq('tipe', 'CUSTOMER')
            eq('activeStatus', 'Y')
            order('nama', 'asc')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('telepon', 'telepon')
                property('hp', 'hp')
                property('perusahaan', 'perusahaan')
            }
        }
    }

    def list(params) {
        def orangs = Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            eq('tipe', 'CUSTOMER')
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('telepon', 'telepon')
                property('hp', 'hp')
                property('perusahaan', 'perusahaan')
            }
        }

        if (!orangs.empty) {
            orangs.each { orang ->
                def perusahaan = [:]
                perusahaan['id'] = orang['perusahaan']['id']
                perusahaan['nama'] = orang['perusahaan']['nama']
                orang['perusahaan'] = perusahaan
            }
        }
        return orangs
    }

    def listNama() {
        def orangs = Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            eq('tipe', 'CUSTOMER')
            eq('activeStatus', 'Y')
            order('nama', 'asc')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('perusahaan', 'perusahaan')
            }
        }

        if (!orangs.empty) {
            orangs.each { orang ->
                def perusahaan = [:]
                perusahaan['id'] = orang['perusahaan']['id']
                perusahaan['nama'] = orang['perusahaan']['nama']
                orang['perusahaan'] = perusahaan
            }
        }
        return orangs
    }

    def save(data) {
        def orang = new Orang()
        orang.tipe = 'CUSTOMER'
        orang.nama = data.nama
        orang.telepon = data.telepon
        orang.hp = data.hp
        orang.activeStatus = 'Y'
        orang.perusahaan = Perusahaan.get(data.perusahaan.id)

        def response = [:]
        if (orang.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = orang.id
        } else {
            response['message'] = 'failed'
            response['error'] = orang.errors.allErrors.code
        }
        return response
    }

    def get(id) {
        def orang = Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            idEq(id)
            eq('tipe', 'CUSTOMER')
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('telepon', 'telepon')
                property('hp', 'hp')
                property('perusahaan', 'perusahaan')
            }
        }

        if (!orang.empty) {
            orang = orang[0]
            def perusahaan = [:]
            perusahaan['id'] = orang['perusahaan']['id']
            perusahaan['nama'] = orang['perusahaan']['nama']
            orang['perusahaan'] = perusahaan
        }
        return orang
    }

    def update(id, data) {
        def orang = Orang.get(id)
        orang.tipe = 'CUSTOMER'
        orang.nama = data.nama
        orang.telepon = data.telepon
        orang.hp = data.hp
        orang.perusahaan = Perusahaan.get(data.perusahaan.id)

        def response = [:]
        if (orang.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = orang.id
        } else {
            response['message'] = 'failed'
            response['error'] = orang.errors.allErrors.code
        }
        return response
    }

    def delete(id) {
        def orang = Orang.get(id)
        orang.activeStatus = 'N'

        def response = [:]
        if (orang.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }

    def checkNama(nama, perusahaanId) {
        return Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                idEq(perusahaanId)
            }
            eq('tipe', 'CUSTOMER')
            eq('nama', nama, [ignoreCase: true])
        }.size()
    }

    def count(params) {
        return Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            eq('tipe', 'CUSTOMER')
            eq('activeStatus', 'Y')
        }.size()
    }
}
