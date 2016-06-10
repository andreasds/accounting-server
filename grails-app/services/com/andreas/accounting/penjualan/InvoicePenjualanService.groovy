package com.andreas.accounting.penjualan

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.saldoawal.InvoiceAwal
import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import com.andreas.accounting.util.ProdukInvoice
import grails.transaction.Transactional
import java.nio.file.Path
import jxl.Workbook
import jxl.format.Colour
import jxl.format.VerticalAlignment
import jxl.write.Label
import jxl.write.Number
import jxl.write.NumberFormat
import jxl.write.WritableWorkbook
import jxl.write.WritableCellFormat
import jxl.write.WritableFont
import jxl.write.WritableSheet
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class InvoicePenjualanService {

    def invoiceTemplate = 'D:/andreas/privacy/Template_Invoice.xlsx'
    def invoiceTemp = 'D:/andreas/privacy/tmp/temporary_invoice.xlsx'
    def invoiceTempOutput = "D:/andreas/privacy/tmp/temporary_invoice_output.xlsx"

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
                eq('tipe', 'CUSTOMER')
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
                property('perusahaan', 'perusahaan')
                property('orang', 'orang')
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
                        property('mataUang', 'mataUang')
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
        def invoice = Invoice.get(id)
        invoice.no = data.no
        invoice.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        invoice.perusahaan = Perusahaan.get(data.perusahaan.id)
        invoice.orang = Orang.get(data.orang.id)

        def response = [:]
        if (invoice.save(flush: true)) {
            data.produkInvoices.each { temp ->
                def produkInvoice = temp.id == 0 ? new ProdukInvoice() : ProdukInvoice.get(temp.id)
                produkInvoice.jumlah = temp.jumlah
                produkInvoice.harga = temp.harga
                produkInvoice.rate = temp.rate
                produkInvoice.invoice = invoice
                produkInvoice.produk = Produk.get(temp.produk.id)
                produkInvoice.mataUang = MataUang.get(temp.mataUang.id)

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

    def download(id) {
        initFormat()
        copyTemplateFile()
        createInvoice(id)

        // Send file to client
        returnFile(invoiceTempOutput)
    }

    def copyTemplateFile() {
        InputStream source = new FileInputStream(new File(invoiceTemplate))
        OutputStream target = null

        if (source != null) {
            try {
                target = new FileOutputStream(new File(invoiceTemp))
                int read = 0
                byte[] bytes = new byte[1024]

                while ((read = source.read(bytes)) != -1) {
                    target.write(bytes, 0, read)
                }
            } catch (IOException ex) {

            } finally {
                if (source != null) {
                    try {
                        source.close();
                    } catch (IOException ex) {

                    }
                }

                if (target != null) {
                    try {
                        target.close();
                    } catch (IOException ex) {

                    }
                }
            }
        }
    }

    WritableFont cellFont
    WritableCellFormat labelFormat, numberFormat

    def initFormat() {
        String thousandPattern = "#,##0.00_);[RED]\\(#,##0.00)\\"
        cellFont = new WritableFont(WritableFont.TAHOMA, 14)

        labelFormat = new WritableCellFormat(cellFont)
        labelFormat.setBackground(Colour.WHITE)
        labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE)

        numberFormat = new WritableCellFormat(cellFont, new NumberFormat(thousandPattern))
        numberFormat.setBackground(Colour.WHITE)
        labelFormat.setVerticalAlignment(VerticalAlignment.CENTRE)
    }

    def createInvoice(id) {
        def outputFile = new File(invoiceTempOutput)
        Workbook existingWorkbook = Workbook.getWorkbook(new java.io.File(invoiceTemp))
        WritableWorkbook workbook = Workbook.createWorkbook(outputFile, existingWorkbook)
        WritableSheet sheet = workbook.getSheet('Invoice')

        def invoice = get(id)
        def dataRow = 10
        invoice.produkInvoices.each { produkInvoice ->
            sheet.insertRow(dataRow)
            sheet.addCell(new Label(1, dataRow, produkInvoice['produk']['kategoriProduk']['kode'] + '-' + produkInvoice['produk']['indeks'], labelFormat))
            sheet.addCell(new Label(2, dataRow, produkInvoice['produk']['deskripsi'], labelFormat))
            sheet.addCell(new Number(3, dataRow, produkInvoice['jumlah'], numberFormat))
            sheet.addCell(new Number(4, dataRow, produkInvoice['mataUang']['kode'] + ' ' + produkInvoice['harga'], numberFormat))
        }

        workbook.write();
        workbook.close();
        existingWorkbook.close();
    }

    /**
     * Returning file as response to front end request
     */
    def returnFile(path) {
        def file = new File(path)

        if (file.exists()) {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=\"report\"")
            response.outputStream << file.bytes
        } else {
            render "error!"
        }
    }
}
