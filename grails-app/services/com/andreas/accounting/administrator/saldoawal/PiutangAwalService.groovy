package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.saldoawal.InvoiceAwal
import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class PiutangAwalService {

    def listAll() {
        return InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    eq('activeStatus', 'Y')
                    order('nama', 'asc')
                }
                orang {
                    perusahaan {
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
                order('tanggal', 'asc')
            }
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('rate', 'rate')
                property('invoice', 'invoice')
                property('mataUang', 'mataUang')
            }
        }
    }

    def list(params, data) {
        def invoiceAwals = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (data['pemilikId'] != 0) {
                        idEq(data['pemilikId'].longValue())
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        if (data.containsKey('perusahaan.nama')) {
                            ilike('nama', "%${data['perusahaan.nama']}%")
                        }

                        eq('activeStatus', 'Y')

                        if (params.sort == 'perusahaan.nama') {
                            order('nama', params.order)
                        }
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('total')) {
                le('jumlah', new BigDecimal(data['total']))
            }

            eq('activeStatus', 'Y')
            if (params.sort == 'total') {
                order('jumlah', params.order)
            }
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                invoice {
                    orang {
                        perusahaan {
                            property('id', 'id')
                        }
                        groupProperty('perusahaan', 'perusahaan')
                    }
                }
                sum('total', 'jumlah')
            }
        }

        if (!invoiceAwals.empty) {
            invoiceAwals.each { invoiceAwal ->
                def temp = Perusahaan.get(invoiceAwal['id'])

                def perusahaan = [:]
                perusahaan['id'] = temp.id
                perusahaan['nama'] = temp.nama
                invoiceAwal['perusahaan'] = perusahaan
                invoiceAwal['rate'] = 1.0g
            }
        }
        return invoiceAwals
    }

    def listByPerusahaan(params, data) {
        def invoiceAwals = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (data['pemilikId'] != 0) {
                        idEq(data['pemilikId'].longValue())
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        if (data.containsKey('perusahaan.nama')) {
                            ilike('nama', "%${data['perusahaan.nama']}%")
                        }

                        if (data['perusahaanId'] != 0) {
                            idEq(data['perusahaanId'].longValue())
                        }

                        eq('activeStatus', 'Y')

                        if (params.sort == 'perusahaan.nama') {
                            order('nama', params.order)
                        }
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }

                if (data.containsKey('invoice.no')) {
                    ilike('no', "%${data['invoice.no']}%")
                }

                if (data.containsKey('invoice.tanggal')) {
                    def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['invoice.tanggal'])
                    between('tanggal', dateTemp, dateTemp + 1)
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'invoice.no') {
                    order('no', params.order)
                }

                if (params.sort == 'invoice.tanggal') {
                    order('tanggal', params.order)
                }
            }

            if (data.containsKey('total')) {
                le('total', new BigDecimal(data['total']))
            }

            eq('activeStatus', 'Y')
            if (params.sort == 'total') {
                order('total', params.order)
            }
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('rate', 'rate')
                property('invoice', 'invoice')
                property('mataUang', 'mataUang')
            }
        }

        if (!invoiceAwals.empty) {
            invoiceAwals.each { invoiceAwal ->
                def invoice = [:]
                invoice['id'] = invoiceAwal['invoice']['id']
                invoice['no'] = invoiceAwal['invoice']['no']
                invoice['tanggal'] = invoiceAwal['invoice']['tanggal']

                def orang = [:]
                orang['id'] = invoiceAwal['invoice']['orang']['id']

                def perusahaan = [:]
                perusahaan['id'] = invoiceAwal['invoice']['orang']['perusahaan']['id']
                perusahaan['nama'] = invoiceAwal['invoice']['orang']['perusahaan']['nama']
                orang['perusahaan'] = perusahaan
                invoice['orang'] = orang
                invoiceAwal['invoice'] = invoice

                def mataUang = [:]
                mataUang['id'] = invoiceAwal['mataUang']['id']
                mataUang['kode'] = invoiceAwal['mataUang']['kode']
                invoiceAwal['mataUang'] = mataUang
            }
        }
        return invoiceAwals
    }

    def save(data) {
        def invoiceAwal = new InvoiceAwal()
        invoiceAwal.jumlah = data.jumlah
        invoiceAwal.rate = data.rate
        invoiceAwal.activeStatus = 'Y'
        invoiceAwal.mataUang = MataUang.get(data.mataUang.id)

        def invoice = new Invoice()
        invoice.no = data.invoice.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.invoice.tanggal)
        invoice.activeStatus = 'Y'
        invoice.perusahaan = Perusahaan.get(data.invoice.perusahaan.id)
        invoice.orang = Orang.get(data.invoice.orang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            invoiceAwal.invoice = Invoice.get(invoice.id)

            if (invoiceAwal.save(flush: true)) {
                response['message'] = 'succeed'
                response['id'] = invoiceAwal.id
            } else {
                response['message'] = 'failed'
                response['error'] = invoiceAwal.errors.allErrors.code
            }
        } else {
            response['message'] = 'failed'
            response['error'] = invoice.errors.allErrors.code
        }
        return response
    }

    def get(id) {
        def invoiceAwal = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    eq('activeStatus', 'Y')
                    order('nama', 'asc')
                }
                orang {
                    perusahaan {
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
                order('tanggal', 'asc')
            }
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('rate', 'rate')
                property('invoice', 'invoice')
                property('mataUang', 'mataUang')
            }
        }

        if (!invoiceAwal.empty) {
            invoiceAwal = invoiceAwal[0]

            def invoice = [:]
            invoice['id'] = invoiceAwal['invoice']['id']
            invoice['no'] = invoiceAwal['invoice']['no']
            invoice['tanggal'] = invoiceAwal['invoice']['tanggal']

            def perusahaan = [:]
            perusahaan['id'] = invoiceAwal['invoice']['perusahaan']['id']
            perusahaan['nama'] = invoiceAwal['invoice']['perusahaan']['nama']
            invoice['perusahaan'] = perusahaan

            def orang = [:]
            orang['id'] = invoiceAwal['invoice']['orang']['id']
            orang['nama'] = invoiceAwal['invoice']['orang']['nama']

            perusahaan = [:]
            perusahaan['id'] = invoiceAwal['invoice']['orang']['perusahaan']['id']
            perusahaan['nama'] = invoiceAwal['invoice']['orang']['perusahaan']['nama']
            orang['perusahaan'] = perusahaan
            invoice['orang'] = orang
            invoiceAwal['invoice'] = invoice

            def mataUang = [:]
            mataUang['id'] = invoiceAwal['mataUang']['id']
            mataUang['kode'] = invoiceAwal['mataUang']['kode']
            invoiceAwal['mataUang'] = mataUang
        }
        return invoiceAwal
    }

    def update(id, data) {
        def invoiceAwal = InvoiceAwal.get(id)
        invoiceAwal.jumlah = data.jumlah
        invoiceAwal.rate = data.rate
        invoiceAwal.mataUang = MataUang.get(data.mataUang.id)

        def invoice = Invoice.get(data.invoice.id)
        invoice.no = data.invoice.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.invoice.tanggal)
        invoice.perusahaan = Perusahaan.get(data.invoice.perusahaan.id)
        invoice.orang = Orang.get(data.invoice.orang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            invoiceAwal.invoice = Invoice.get(invoice.id)

            if (invoiceAwal.save(flush: true)) {
                response['message'] = 'succeed'
                response['id'] = invoiceAwal.id
            } else {
                response['message'] = 'failed'
                response['error'] = invoiceAwal.errors.allErrors.code
            }
        } else {
            response['message'] = 'failed'
            response['error'] = invoice.errors.allErrors.code
        }
        return response
    }

    def delete(id) {
        def invoiceAwal = InvoiceAwal.get(id)
        invoiceAwal.activeStatus = 'N'

        def invoice = Invoice.get(invoiceAwal.invoice.id)
        invoice.activeStatus = 'N'

        def response = [:]
        if (invoice.save(flush: true)) {
            if (invoiceAwal.save(flush: true)) {
                response['message'] = 'succeed'
            } else {
                response['message'] = 'failed'
            }
        } else {
            response['message'] = 'failed'
        }
        return response
    }

    def checkNo(no, perusahaanId) {
        return InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    idEq(perusahaanId)
                }
                orang {
                    eq('tipe', 'CUSTOMER')
                }
                eq('no', no, [ignoreCase: true])
            }
        }.size()
    }

    def getTotal(pemilikId) {
        def total = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (pemilikId != 0) {
                        idEq(pemilikId)
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                sum('total', 'total')
            }
        }[0]['total']
        return total != null ? total : 0
    }

    def getTotalByPerusahaan(perusahaanId, pemilikId) {
        def total = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (pemilikId != 0) {
                        idEq(pemilikId)
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        if (perusahaanId != 0) {
                            idEq(perusahaanId)
                        }
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                sum('total', 'total')
            }
        }[0]['total']
        return total != null ? total : 0
    }

    def count(params, data) {
        return InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (data['pemilikId'] != 0) {
                        idEq(data['pemilikId'].longValue())
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        if (data.containsKey('perusahaan.nama')) {
                            ilike('nama', "%${data['perusahaan.nama']}%")
                        }
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('total')) {
                le('jumlah', new BigDecimal(data['total']))
            }

            eq('activeStatus', 'Y')
            projections {
                invoice {
                    orang {
                        groupProperty('perusahaan', 'perusahaan')
                    }
                }
                sum('total', 'jumlah')
            }
        }.size()
    }

    def countByPerusahaan(params, data) {
        return InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (data['pemilikId'] != 0) {
                        idEq(data['pemilikId'].longValue())
                    }
                    eq('activeStatus', 'Y')
                }
                orang {
                    perusahaan {
                        if (data.containsKey('perusahaan.nama')) {
                            ilike('nama', "%${data['perusahaan.nama']}%")
                        }

                        if (data['perusahaanId'] != 0) {
                            idEq(data['perusahaanId'].longValue())
                        }

                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'CUSTOMER')
                    eq('activeStatus', 'Y')
                }

                if (data.containsKey('invoice.no')) {
                    ilike('no', "%${data['invoice.no']}%")
                }

                if (data.containsKey('invoice.tanggal')) {
                    def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['invoice.tanggal'])
                    between('tanggal', dateTemp, dateTemp + 1)
                }

                eq('activeStatus', 'Y')
            }

            if (data.containsKey('jumlah')) {
                le('total', new BigDecimal(data['jumlah']))
            }

            eq('activeStatus', 'Y')
        }.size()
    }
}
