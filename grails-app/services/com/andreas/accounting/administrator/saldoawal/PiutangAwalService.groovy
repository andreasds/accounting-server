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

    def list(params) {
        def invoiceAwals = InvoiceAwal.withCriteria {
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
            order(params.sort, params.order)
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

    def getTotal(perusahaanId) {
        def total = InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    if (perusahaanId != 0) {
                        idEq(perusahaanId)
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

    def count(params) {
        return InvoiceAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
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
        }.size()
    }
}
