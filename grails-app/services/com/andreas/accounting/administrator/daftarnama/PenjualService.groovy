package com.andreas.accounting.administrator.daftarnama

import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.daftarnama.Perusahaan
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class PenjualService {

    def dataSource

    def listAll() {
        return Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            eq('tipe', 'VENDOR')
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

    def list(params, data) {
        def orangs = Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (data.containsKey('perusahaan.nama')) {
                    ilike('nama', "%${data['perusahaan.nama']}%")
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'perusahaan.nama') {
                    order('nama', params.order)
                }
            }

            if (data.containsKey('nama')) {
                ilike('nama', "%${data['nama']}%")
            }

            if (data.containsKey('telepon')) {
                ilike('telepon', "%${data['telepon']}%")
            }

            if (data.containsKey('hp')) {
                ilike('hp', "%${data['hp']}%")
            }

            eq('tipe', 'VENDOR')
            eq('activeStatus', 'Y')
            if (params.sort != 'perusahaan.nama') {
                order(params.sort, params.order)
            }
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
            eq('tipe', 'VENDOR')
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

    def listNamaHutang(penjualId, pemilikId) {
        def db = new Sql(dataSource)

        def query = "SELECT person.id \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.orang_id AS orang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y' AND company.id=${pemilikId}) \n\
                AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR') \n\
            AND company.active_status='Y' \n\
            AND (total - bayar <> 0) \n\
            GROUP BY person.id"

        def penjuals = []
        db.rows(query).each { penjual ->
            penjuals.add(penjual.id)
        }

        if (penjualId != 0) {
            if (penjuals.find { it == penjualId } == null) {
                penjuals.add(penjualId)
            }
        }

        def orangs = []
        if (!penjuals.empty) {
            orangs = Orang.withCriteria {
                resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                perusahaan {
                    eq('activeStatus', 'Y')
                }
                'in'('id', penjuals)
                eq('tipe', 'VENDOR')
                eq('activeStatus', 'Y')
                order('nama', 'asc')
                projections {
                    property('id', 'id')
                    property('nama', 'nama')
                    property('perusahaan', 'perusahaan')
                }
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
        orang.tipe = 'VENDOR'
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
            eq('tipe', 'VENDOR')
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
        orang.tipe = 'VENDOR'
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
            eq('tipe', 'VENDOR')
            eq('nama', nama, [ignoreCase: true])
        }.size()
    }

    def count(params, data) {
        return Orang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (data.containsKey('perusahaan.nama')) {
                    ilike('nama', "%${data['perusahaan.nama']}%")
                }
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('nama')) {
                ilike('nama', "%${data['nama']}%")
            }

            if (data.containsKey('telepon')) {
                ilike('telepon', "%${data['telepon']}%")
            }

            if (data.containsKey('hp')) {
                ilike('hp', "%${data['hp']}%")
            }

            eq('tipe', 'VENDOR')
            eq('activeStatus', 'Y')
        }.size()
    }
}
