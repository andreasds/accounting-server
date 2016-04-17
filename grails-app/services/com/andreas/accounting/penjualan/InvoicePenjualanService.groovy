package com.andreas.accounting.penjualan

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.saldoawal.InvoiceAwal
import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import com.andreas.accounting.util.ProdukInvoice
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class InvoicePenjualanService {

    def listAll() {

    }

    def list(params, data) {
        def invoiceAwals = InvoiceAwal.withCriteria {
            eq('activeStatus', 'Y')
            projections {
                invoice {
                    property('id')
                }
            }
        }

        def invoices = Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
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
            not {
                'in'('id', invoiceAwals)
            }
            eq('activeStatus', 'Y')
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('no', 'no')
                property('tanggal', 'tanggal')
                property('orang', 'orang')
            }
        }

        if (!invoices.empty) {
            invoices.each { invoice ->
                def perusahaan = [:]
                perusahaan['id'] = invoice['orang']['perusahaan']['id']
                perusahaan['nama'] = invoice['orang']['perusahaan']['nama']

                def orang = [:]
                orang['id'] = invoice['orang']['id']
                orang['perusahaan'] = perusahaan
                invoice['orang'] = orang
            }
        }
        return invoices
    }

    def save(data) {
        def invoice = new Invoice()
        invoice.no = data.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        invoice.activeStatus = 'Y'
        invoice.perusahaan = Perusahaan.get(data.perusahaan.id)
        invoice.orang = Orang.get(data.orang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            data.produkInvoices.each { temp ->
                def produkInvoice = new ProdukInvoice()
                produkInvoice.jumlah = temp.jumlah
                produkInvoice.harga = temp.harga
                produkInvoice.rate = temp.rate
                produkInvoice.invoice = invoice
                produkInvoice.produk = Produk.get(temp.produk.id)
                produkInvoice.mataUang = MataUang.get(temp.mataUang.id)

                if (produkInvoice.save(flush: true)) {
                    response['message'] = 'succeed'
                    response['id'] = invoice.id
                } else {
                    response['message'] = 'failed'
                    response['error'] = produkInvoice.errors.allErrors.code
                }
            }
        } else {
            response['message'] = 'failed'
            response['error'] = invoice.errors.allErrors.code
        }
        return response
    }

    def get(id) {

    }

    def update(id, data) {

    }

    def delete(id) {

    }

    def checkNo(no, pemilikId) {
        return Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                idEq(pemilikId)
            }
            orang {
                eq('tipe', 'CUSTOMER')
            }
            eq('no', no, [ignoreCase: true])
        }.size()
    }

    def count(params, data) {
        return 1
    }
}
