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
                    if (data.containsKey('orang.perusahaan.nama')) {
                        ilike('nama', "%${data['orang.perusahaan.nama']}%")
                    }

                    eq('activeStatus', 'Y')

                    if (params.sort == 'orang.perusahaan.nama') {
                        order('nama', params.order)
                    }
                }
                eq('tipe', 'CUSTOMER')
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
                        property('harga', 'harga')
                        property('rate', 'rate')
                    }
                }
                invoiceModel['produkInvoices'] = produkInvoices
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
        def invoiceModel = Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
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
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('no', 'no')
                property('tanggal', 'tanggal')
                property('perusahaan', 'perusahaan')
                property('orang', 'orang')
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
                    property('mataUang', 'mataUang')
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

                def mataUang = [:]
                mataUang['id'] = produkInvoice['mataUang']['id']
                mataUang['kode'] = produkInvoice['mataUang']['kode']
                produkInvoice['mataUang'] = mataUang
            }
            invoiceModel['produkInvoices'] = produkInvoices
        }
        return invoiceModel
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
                eq('tipe', 'CUSTOMER')
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
