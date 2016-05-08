package com.andreas.accounting.pembelian

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.saldoawal.InvoiceAwal
import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import com.andreas.accounting.util.Pembayaran
import com.andreas.accounting.util.ProdukInvoice
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class InvoicePembelianService {

    def dataSource

    def listAll() {
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
                eq('activeStatus', 'Y')
            }
            orang {
                perusahaan {
                    eq('activeStatus', 'Y')
                }
                eq('tipe', 'VENDOR')
                eq('activeStatus', 'Y')
            }

            not {
                'in'('id', invoiceAwals)
            }
            eq('activeStatus', 'Y')
            order('tanggal', 'desc')
            projections {
                property('id', 'id')
                property('no', 'no')
                property('tanggal', 'tanggal')
                property('rate', 'rate')
                property('perusahaan', 'perusahaan')
                property('orang', 'orang')
                property('mataUang', 'mataUang')
            }
        }

        if (!invoices.empty) {
            invoices.each { invoiceModel ->
                def produkInvoices = ProdukInvoice.withCriteria {
                    resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                    invoice {
                        idEq(invoiceModel['id'])
                        eq('activeStatus', 'Y')
                    }
                    produk {
                        kategoriProduk {
                            eq('activeStatus', 'Y')
                        }
                        satuan {
                            eq('activeStatus', 'Y')
                        }
                        eq('activeStatus', 'Y')
                    }
                    projections {
                        property('id', 'id')
                        property('jumlah', 'jumlah')
                        property('harga', 'harga')
                        property('rate', 'rate')
                        property('produk', 'produk')
                    }
                }
                invoiceModel['produkInvoices'] = produkInvoices
            }
        }
        return invoices
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
                    if (data.containsKey('orang.perusahaan.nama')) {
                        ilike('nama', "%${data['orang.perusahaan.nama']}%")
                    }

                    eq('activeStatus', 'Y')

                    if (params.sort == 'orang.perusahaan.nama') {
                        order('nama', params.order)
                    }
                }
                eq('tipe', 'VENDOR')
                eq('activeStatus', 'Y')
            }

            not {
                'in'('id', invoiceAwals)
            }

            if (data.containsKey('no')) {
                ilike('no', "%${data['no']}%")
            }

            if (data.containsKey('tanggal')) {
                def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['tanggal'])
                between('tanggal', dateTemp, dateTemp + 1)
            }

            eq('activeStatus', 'Y')

            if (params.sort == 'no') {
                order('no', params.order)
            }

            if (params.sort == 'tanggal') {
                order('tanggal', params.order)
            }

            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('no', 'no')
                property('tanggal', 'tanggal')
                property('rate', 'rate')
                property('orang', 'orang')
            }
        }

        if (!invoices.empty) {
            invoices.each { invoiceModel ->
                def perusahaan = [:]
                perusahaan['id'] = invoiceModel['orang']['perusahaan']['id']
                perusahaan['nama'] = invoiceModel['orang']['perusahaan']['nama']

                def orang = [:]
                orang['id'] = invoiceModel['orang']['id']
                orang['perusahaan'] = perusahaan
                invoiceModel['orang'] = orang

                def produkInvoices = ProdukInvoice.withCriteria {
                    resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                    invoice {
                        idEq(invoiceModel['id'])
                        eq('activeStatus', 'Y')
                    }
                    produk {
                        kategoriProduk {
                            eq('activeStatus', 'Y')
                        }
                        satuan {
                            eq('activeStatus', 'Y')
                        }
                        eq('activeStatus', 'Y')
                    }
                    projections {
                        property('id', 'id')
                        property('jumlah', 'jumlah')
                        property('rate', 'rate')
                        property('harga', 'harga')
                    }
                }
                invoiceModel['produkInvoices'] = produkInvoices
            }
        }
        return invoices
    }

    def listHutang(pembayaranId, penjualId, pemilikId) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.invoice_id AS invoice_id, \n\
            total - bayar AS jumlah \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.id AS invoice_id, \n\
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
            WHERE (person.active_status='Y' AND person.tipe='VENDOR' AND person.id=${penjualId}) \n\
            AND company.active_status='Y' \n\
            AND (total - bayar <> 0)"

        def invoices = db.rows(query)

        if (pembayaranId != 0) {
            def pembayaran = Pembayaran.get(pembayaranId)
            if (invoices.find { it['invoice_id'] == pembayaran.invoice.id } == null) {
                def invoice = [:]
                invoice['invoice_id'] = pembayaran.invoice.id
                invoice['jumlah'] = pembayaran.jumlah
                invoices.add(invoice)
            }
        }

        if (!invoices.empty) {
            invoices.each { invoice ->
                def temp = Invoice.get(invoice['invoice_id'])
                invoice.remove('invoice_id')
                invoice['id'] = temp.id
                invoice['no'] = temp.no

                temp = MataUang.get(temp.mataUang.id)
                def mataUang = [:]
                mataUang['id'] = temp.id
                mataUang['kode'] = temp.kode
                invoice['mataUang'] = mataUang
            }
        }

        return invoices
    }

    def save(data) {
        def invoice = new Invoice()
        invoice.no = data.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        invoice.rate = data.rate
        invoice.activeStatus = 'Y'
        invoice.perusahaan = Perusahaan.get(data.perusahaan.id)
        invoice.orang = Orang.get(data.orang.id)
        invoice.mataUang = MataUang.get(data.mataUang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            data.produkInvoices.each { temp ->
                def produkInvoice = new ProdukInvoice()
                produkInvoice.jumlah = temp.jumlah
                produkInvoice.harga = temp.harga
                produkInvoice.rate = data.rate
                produkInvoice.invoice = invoice
                produkInvoice.produk = Produk.get(temp.produk.id)

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
        def invoiceModel = Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            orang {
                perusahaan {
                    eq('activeStatus', 'Y')
                }
                eq('tipe', 'VENDOR')
                eq('activeStatus', 'Y')
            }
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('no', 'no')
                property('tanggal', 'tanggal')
                property('rate', 'rate')
                property('perusahaan', 'perusahaan')
                property('orang', 'orang')
                property('mataUang', 'mataUang')
            }
        }

        if (!invoiceModel.empty) {
            invoiceModel = invoiceModel[0]

            def perusahaan = [:]
            perusahaan['id'] = invoiceModel['perusahaan']['id']
            perusahaan['nama'] = invoiceModel['perusahaan']['nama']
            invoiceModel['perusahaan'] = perusahaan

            def orang = [:]
            orang['id'] = invoiceModel['orang']['id']
            orang['nama'] = invoiceModel['orang']['nama']

            perusahaan = [:]
            perusahaan['id'] = invoiceModel['orang']['perusahaan']['id']
            perusahaan['nama'] = invoiceModel['orang']['perusahaan']['nama']
            orang['perusahaan'] = perusahaan
            invoiceModel['orang'] = orang

            def mataUang = [:]
            mataUang['id'] = invoiceModel['mataUang']['id']
            mataUang['kode'] = invoiceModel['mataUang']['kode']
            invoiceModel['mataUang'] = mataUang

            def produkInvoices = ProdukInvoice.withCriteria {
                resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
                invoice {
                    idEq(invoiceModel['id'])
                    eq('activeStatus', 'Y')
                }
                produk {
                    kategoriProduk {
                        eq('activeStatus', 'Y')
                    }
                    satuan {
                        eq('activeStatus', 'Y')
                    }
                    eq('activeStatus', 'Y')
                }
                projections {
                    property('id', 'id')
                    property('jumlah', 'jumlah')
                    property('harga', 'harga')
                    property('rate', 'rate')
                    property('produk', 'produk')
                }
            }

            produkInvoices.each { produkInvoice ->
                def produk = [:]
                produk['id'] = produkInvoice['produk']['id']
                produk['indeks'] = produkInvoice['produk']['indeks']
                produk['deskripsi'] = produkInvoice['produk']['deskripsi']

                def kategoriProduk = [:]
                kategoriProduk['id'] = produkInvoice['produk']['kategoriProduk']['id']
                kategoriProduk['kode'] = produkInvoice['produk']['kategoriProduk']['kode']
                produk['kategoriProduk'] = kategoriProduk
                produkInvoice['produk'] = produk
            }
            invoiceModel['produkInvoices'] = produkInvoices
        }
        return invoiceModel
    }

    def update(id, data) {
        def invoice = Invoice.get(id)
        invoice.no = data.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        invoice.rate = data.rate
        invoice.perusahaan = Perusahaan.get(data.perusahaan.id)
        invoice.orang = Orang.get(data.orang.id)
        invoice.mataUang = MataUang.get(data.mataUang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            data.produkInvoices.each { temp ->
                def produkInvoice = temp.id == 0 ? new ProdukInvoice() : ProdukInvoice.get(temp.id)
                produkInvoice.jumlah = temp.jumlah
                produkInvoice.harga = temp.harga
                produkInvoice.rate = data.rate
                produkInvoice.invoice = invoice
                produkInvoice.produk = Produk.get(temp.produk.id)

                if (temp.removed) {
                    if (produkInvoice.delete(flush: true)) {
                        response['message'] = 'succeed'
                        response['id'] = invoice.id
                    } else {
                        response['message'] = 'failed'
                        response['error'] = produkInvoice.errors.allErrors.code
                    }
                } else {
                    if (produkInvoice.save(flush: true)) {
                        response['message'] = 'succeed'
                        response['id'] = invoice.id
                    } else {
                        response['message'] = 'failed'
                        response['error'] = produkInvoice.errors.allErrors.code
                    }
                }
            }
        } else {
            response['message'] = 'failed'
            response['error'] = invoice.errors.allErrors.code
        }
        return response
    }

    def delete(id) {
        def invoice = Invoice.get(id)
        invoice.activeStatus = 'N'

        def response = [:]
        if (invoice.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }

    def checkNo(no, pemilikId) {
        return Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                idEq(pemilikId)
            }
            orang {
                eq('tipe', 'VENDOR')
            }
            eq('no', no, [ignoreCase: true])
        }.size()
    }

    def count(params, data) {
        def invoiceAwals = InvoiceAwal.withCriteria {
            eq('activeStatus', 'Y')
            projections {
                invoice {
                    property('id')
                }
            }
        }

        return Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (data['pemilikId'] != 0) {
                    idEq(data['pemilikId'].longValue())
                }
                eq('activeStatus', 'Y')
            }
            orang {
                perusahaan {
                    if (data.containsKey('orang.perusahaan.nama')) {
                        ilike('nama', "%${data['orang.perusahaan.nama']}%")
                    }

                    eq('activeStatus', 'Y')
                }
                eq('tipe', 'VENDOR')
                eq('activeStatus', 'Y')
            }

            not {
                'in'('id', invoiceAwals)
            }

            if (data.containsKey('no')) {
                ilike('no', "%${data['no']}%")
            }

            if (data.containsKey('tanggal')) {
                def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['tanggal'])
                between('tanggal', dateTemp, dateTemp + 1)
            }

            eq('activeStatus', 'Y')
        }.size()
    }
}
