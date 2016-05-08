package com.andreas.accounting.pembelian

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import com.andreas.accounting.administrator.saldoawal.InvoiceAwal
import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import com.andreas.accounting.util.Pembayaran
import com.andreas.accounting.util.ProdukInvoice
import com.andreas.accounting.util.Rekening
import grails.transaction.Transactional
import groovy.sql.Sql
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class HutangService {

    def dataSource

    def listAll() {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.orang_id AS orang_id, \n\
            hutang.mata_uang_id AS mata_uang_id, \n\
            SUM(total) AS total, \n\
            SUM(bayar) AS bayar \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y') \n\
                AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR') \n\
            AND company.active_status='Y' \n\
            AND (total - bayar <> 0) \n\
            GROUP BY person.id, hutang.mata_uang_id"

        def hutangs = db.rows(query)

        if (!hutangs.empty) {
            hutangs.each { hutang ->
                def temp = Orang.get(hutang['orang_id'])
                hutang.remove('orang_id')
                def orang = temp

                temp = Perusahaan.get(temp.perusahaan.id)
                def perusahaan = temp
                orang['perusahaan'] = perusahaan
                hutang['orang'] = orang

                temp = MataUang.get(hutang['mata_uang_id'])
                hutang.remove('mata_uang_id')
                def mataUang = temp
                hutang['mataUang'] = mataUang
            }
        }
        return hutangs
    }

    def list(params, data) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.orang_id AS orang_id, \n\
            hutang.mata_uang_id AS mata_uang_id, \n\
            SUM(total) AS total, \n\
            SUM(bayar) AS bayar \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (data['pemilikId'] != 0) {
            query += " AND company.id=${data['pemilikId'].longValue()}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR') \n\
            AND company.active_status='Y' "

        if (data.containsKey('orang.nama')) {
            query += "AND LOWER(person.nama) LIKE LOWER('%${data['orang.nama']}%') "
        }

        if (data.containsKey('orang.perusahaan.nama')) {
            query += "AND LOWER(company.nama) LIKE LOWER('%${data['orang.perusahaan.nama']}%') "
        }

        if (data.containsKey('total')) {
            query += "AND SUM(total_temp) <= ${data['total']} "
        }

        if (data.containsKey('bayar')) {
            query += "AND SUM(bayar_temp) <= ${data['bayar']} "
        }

        if (data.containsKey('sisa')) {
            query += "AND SUM(sisa) <= ${data['sisa']} "
        }

        query += "AND (total - bayar <> 0) \n\
            GROUP BY person.id, hutang.mata_uang_id "

        if (params.sort == 'orang.nama') {
            query += "ORDER BY person.nama ${params.order} "
        }

        if (params.sort == 'orang.perusahaan.nama') {
            query += "ORDER BY company.nama ${params.order} "
        }

        if (params.sort == 'total') {
            query += "ORDER BY SUM(total_temp) ${params.order} "
        }

        if (params.sort == 'bayar') {
            query += "ORDER BY SUM(bayar_temp) ${params.order} "
        }

        if (params.sort == 'sisa') {
            query += "ORDER BY SUM(sisa) ${params.order} "
        }

        query += "LIMIT ${params.offset}, ${params.max}"

        def hutangs = db.rows(query)

        if (!hutangs.empty) {
            hutangs.each { hutang ->
                def temp = Orang.get(hutang['orang_id'])
                hutang.remove('orang_id')
                def orang = [:]
                orang['id'] = temp.id
                orang['nama'] = temp.nama

                temp = Perusahaan.get(temp.perusahaan.id)
                def perusahaan = [:]
                perusahaan['id'] = temp.id
                perusahaan['nama'] = temp.nama
                orang['perusahaan'] = perusahaan
                hutang['orang'] = orang

                temp = MataUang.get(hutang['mata_uang_id'])
                hutang.remove('mata_uang_id')
                def mataUang = [:]
                mataUang['id'] = temp.id
                mataUang['kode'] = temp.kode
                hutang['mataUang'] = mataUang
            }
        }
        return hutangs
    }

    def listByOrang(params, data) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.invoice_id AS invoice_id, \n\
            hutang.mata_uang_id AS mata_uang_id, \n\
            hutang.total AS total, \n\
            hutang.bayar AS bayar \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.id AS invoice_id, \n\
                invoice.no AS invoice_no, \n\
                invoice.tanggal AS invoice_tanggal, \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (data['pemilikId'] != 0) {
            query += " AND company.id=${data['pemilikId'].longValue()}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR'"

        if (data['penjualId'] != 0) {
            query += " AND person.id=${data['penjualId'].longValue()}"
        }

        query += ") AND company.active_status='Y' "

        if (data.containsKey('invoice.no')) {
            query += "AND LOWER(invoice_no) LIKE LOWER('%${data['invoice.no']}%') "
        }

        if (data.containsKey('invoice.tanggal')) {
            query += "AND DATE(invoice_tanggal) = DATE('${Date.parse('MMM dd, yyyy HH:mm:ss a', data['invoice.tanggal']).format('yyyy-MM-dd')}') "
        }

        if (data.containsKey('total')) {
            query += "AND total_temp <= ${data['total']} "
        }

        if (data.containsKey('bayar')) {
            query += "AND bayar_temp <= ${data['bayar']} "
        }

        if (data.containsKey('sisa')) {
            query += "AND sisa <= ${data['sisa']} "
        }

        query += "AND (total - bayar <> 0) "

        if (params.sort == 'invoice.no') {
            query += "ORDER BY invoice_no ${params.order} "
        }

        if (params.sort == 'invoice.tanggal') {
            query += "ORDER BY invoice_tanggal ${params.order} "
        }

        if (params.sort == 'total') {
            query += "ORDER BY total_temp ${params.order} "
        }

        if (params.sort == 'bayar') {
            query += "ORDER BY bayar_temp ${params.order} "
        }

        if (params.sort == 'sisa') {
            query += "ORDER BY sisa ${params.order} "
        }

        query += "LIMIT ${params.offset}, ${params.max}"

        def hutangs = db.rows(query)

        if (!hutangs.empty) {
            hutangs.each { hutang ->
                def temp = Invoice.get(hutang['invoice_id'])
                hutang.remove('invoice_id')
                def invoice = [:]
                invoice['id'] = temp.id
                invoice['no'] = temp.no
                invoice['tanggal'] = temp.tanggal
                hutang['invoice'] = invoice

                temp = MataUang.get(hutang['mata_uang_id'])
                hutang.remove('mata_uang_id')
                def mataUang = [:]
                mataUang['id'] = temp.id
                mataUang['kode'] = temp.kode
                hutang['mataUang'] = mataUang
            }
        }
        return hutangs
    }

    def listBayar(params, data) {
        def pembayarans = Pembayaran.withCriteria {
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
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'VENDOR')
                    eq('activeStatus', 'Y')
                }

                if (data.containsKey('invoice.no')) {
                    ilike('no', "%${data['invoice.no']}%")
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'invoice.no') {
                    order('no', params.order)
                }
            }
            rekening {
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('tanggal')) {
                def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['tanggal'])
                between('tanggal', dateTemp, dateTemp + 1)
            }

            if (data.containsKey('jumlah')) {
                le('jumlah', new BigDecimal(data['jumlah']))
            }

            if (data.containsKey('deskripsi')) {
                ilike('deskripsi', "%${data['deskripsi']}%")
            }

            eq('activeStatus', 'Y')

            if (params.sort == 'tanggal') {
                order('tanggal', params.order)
            }

            if (params.sort == 'jumlah') {
                order('jumlah', params.order)
            }

            if (params.sort == 'deskripsi') {
                order('deskripsi', params.order)
            }

            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('tanggal', 'tanggal')
                property('jumlah', 'jumlah')
                property('deskripsi', 'deskripsi')
                property('invoice', 'invoice')
            }
        }

        if (!pembayarans.empty) {
            pembayarans.each { pembayaran ->
                def invoice = [:]
                invoice['id'] = pembayaran['invoice']['id']
                invoice['no'] = pembayaran['invoice']['no']
                pembayaran['invoice'] = invoice
            }
        }
        return pembayarans
    }

    def save(data) {
        def response = [:]
        if(!data.pembayarans.empty) {
            data.pembayarans.each { pembayaranTemp ->
                def pembayaran = new Pembayaran()
                pembayaran.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
                pembayaran.jumlah = pembayaranTemp.bayar
                pembayaran.rate = pembayaranTemp.rate
                pembayaran.deskripsi = data.deskripsi
                pembayaran.activeStatus = 'Y'
                pembayaran.invoice = Invoice.get(pembayaranTemp.invoice.id)
                pembayaran.rekening = Rekening.get(data.rekening.id)
                pembayaran.mataUang = MataUang.get(pembayaranTemp.mataUang.id)

                if (pembayaran.save(flush: true)) {
                    response['message'] = 'succeed'
                } else {
                    response['message'] = 'failed'
                    response['error'] = invoice.errors.allErrors.code
                }
            }
        }
        return response
    }

    def get(id) {
        def pembayaran = Pembayaran.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
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
                eq('activeStatus', 'Y')
            }
            rekening {
                eq('activeStatus', 'Y')
            }
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('tanggal', 'tanggal')
                property('jumlah', 'bayar')
                property('rate', 'rate')
                property('deskripsi', 'deskripsi')
                property('invoice', 'invoice')
                property('rekening', 'rekening')
                property('mataUang', 'mataUang')
            }
        }

        if (!pembayaran.empty) {
            pembayaran = pembayaran[0]

            def jumlah = InvoiceAwal.withCriteria {
                invoice {
                    idEq(pembayaran['invoice']['id'])
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
                projections {
                    property('jumlah')
                }
            }

            if (jumlah[0] == null) {
                jumlah = ProdukInvoice.withCriteria {
                    invoice {
                        idEq(pembayaran['invoice']['id'])
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
                        sum('jumlah')
                    }
                }
            }

            def bayar = Pembayaran.withCriteria {
                invoice {
                    idEq(pembayaran['invoice']['id'])
                    eq('activeStatus', 'Y')
                }
                rekening {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
                projections {
                    sum('jumlah')
                }
            }
            pembayaran['jumlah'] = jumlah[0] - bayar[0] + pembayaran['bayar']

            def invoice = [:]
            invoice['id'] = pembayaran['invoice']['id']
            invoice['no'] = pembayaran['invoice']['no']

            def perusahaan = [:]
            perusahaan['id'] = pembayaran['invoice']['perusahaan']['id']
            perusahaan['nama'] = pembayaran['invoice']['perusahaan']['nama']
            invoice['perusahaan'] = perusahaan

            def orang = [:]
            orang['id'] = pembayaran['invoice']['orang']['id']
            orang['nama'] = pembayaran['invoice']['orang']['nama']

            perusahaan = [:]
            perusahaan['id'] = pembayaran['invoice']['orang']['perusahaan']['id']
            perusahaan['nama'] = pembayaran['invoice']['orang']['perusahaan']['nama']
            orang['perusahaan'] = perusahaan
            invoice['orang'] = orang

            def mataUang = [:]
            mataUang['id'] = pembayaran['invoice']['mataUang']['id']
            mataUang['kode'] = pembayaran['invoice']['mataUang']['kode']
            invoice['mataUang'] = mataUang
            pembayaran['invoice'] = invoice

            def rekening = [:]
            rekening['id'] = pembayaran['rekening']['id']
            rekening['nama'] = pembayaran['rekening']['nama']
            pembayaran['rekening'] = rekening

            mataUang = [:]
            mataUang['id'] = pembayaran['mataUang']['id']
            mataUang['kode'] = pembayaran['mataUang']['kode']
            pembayaran['mataUang'] = mataUang
        }
        return pembayaran
    }

    def update(id, data) {
        def pembayaran = Pembayaran.get(id)
        pembayaran.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        pembayaran.jumlah = data.bayar
        pembayaran.rate = data.rate
        pembayaran.deskripsi = data.deskripsi
        pembayaran.invoice = Invoice.get(data.invoice.id)
        pembayaran.rekening = Rekening.get(data.rekening.id)
        pembayaran.mataUang = MataUang.get(data.mataUang.id)

        def response = [:]
        if (pembayaran.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = pembayaran.id
        } else {
            response['message'] = 'failed'
            response['error'] = pembayaran.errors.allErrors.code
        }

        return response
    }

    def delete(id) {
        def pembayaran = Pembayaran.get(id)
        pembayaran.activeStatus = 'N'

        def response = [:]
        if (pembayaran.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }

        return response
    }

    def getTotal(pemilikId) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            SUM(sisa) AS sisa \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (pemilikId != 0) {
            query += " AND company.id=${pemilikId}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR') \n\
            AND company.active_status='Y' \n\
            AND (total - bayar <> 0) \n\
            GROUP BY person.id, hutang.mata_uang_id"

        def totals = db.rows(query)
        def total = 0
        if (!totals.empty) {
            totals.each { sisa ->
                total += sisa['sisa']
            }
        }

        return total
    }

    def getTotalByOrang(penjualId, pemilikId) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            SUM(sisa) AS sisa \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.id AS invoice_id, \n\
                invoice.no AS invoice_no, \n\
                invoice.tanggal AS invoice_tanggal, \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (pemilikId != 0) {
            query += " AND company.id=${pemilikId}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR'"

        if (penjualId != 0) {
            query += " AND person.id=${penjualId}"
        }

        query += ") AND company.active_status='Y' \n\
            AND (total - bayar <> 0)"

        def total = db.rows(query)[0]['sisa']
        return total != null ? total : 0
    }

    def count(params, data) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.orang_id AS orang_id, \n\
            hutang.mata_uang_id AS mata_uang_id, \n\
            SUM(total) AS total, \n\
            SUM(bayar) AS bayar \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (data['pemilikId'] != 0) {
            query += " AND company.id=${data['pemilikId'].longValue()}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR') \n\
            AND company.active_status='Y' "

        if (data.containsKey('orang.nama')) {
            query += "AND LOWER(person.nama) LIKE LOWER('%${data['orang.nama']}%') "
        }

        if (data.containsKey('orang.perusahaan.nama')) {
            query += "AND LOWER(company.nama) LIKE LOWER('%${data['orang.perusahaan.nama']}%') "
        }

        if (data.containsKey('total')) {
            query += "AND SUM(total_temp) <= ${data['total']} "
        }

        if (data.containsKey('bayar')) {
            query += "AND SUM(bayar_temp) <= ${data['bayar']} "
        }

        if (data.containsKey('sisa')) {
            query += "AND SUM(sisa) <= ${data['sisa']} "
        }

        query += "AND (total - bayar <> 0) \n\
            GROUP BY person.id, hutang.mata_uang_id"

        return db.rows(query).size()
    }

    def countByOrang(params, data) {
        def db = new Sql(dataSource)

        def query = "SELECT \n\
            hutang.invoice_id AS invoice_id, \n\
            hutang.mata_uang_id AS mata_uang_id, \n\
            hutang.total AS total, \n\
            hutang.bayar AS bayar \n\
            FROM \n\
            ( \n\
                SELECT \n\
                invoice.id AS invoice_id, \n\
                invoice.no AS invoice_no, \n\
                invoice.tanggal AS invoice_tanggal, \n\
                invoice.orang_id AS orang_id, \n\
                invoice.mata_uang_id AS mata_uang_id, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah) ELSE invoice_initial.jumlah END), 0) AS total, \n\
                IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) AS total_temp, \n\
                IFNULL(SUM(payment.jumlah), 0) AS bayar, \n\
                IFNULL(SUM(payment.jumlah * payment.rate), 0) AS bayar_temp, \n\
                (IFNULL((CASE WHEN invoice_initial.id IS NULL THEN SUM(item_invoice.harga * item_invoice.jumlah * item_invoice.rate) ELSE invoice_initial.jumlah * invoice_initial.rate END), 0) - IFNULL(SUM(payment.jumlah * payment.rate), 0)) AS sisa \n\
                FROM invoice \n\
                LEFT JOIN company ON invoice.perusahaan_id=company.id \n\
                LEFT JOIN invoice_initial ON invoice_initial.invoice_id=invoice.id \n\
                LEFT JOIN item_invoice ON item_invoice.invoice_id=invoice.id \n\
                LEFT JOIN payment ON payment.invoice_id=invoice.id \n\
                WHERE invoice.active_status='Y' \n\
                AND (company.active_status='Y'"

        if (data['pemilikId'] != 0) {
            query += " AND company.id=${data['pemilikId'].longValue()}"
        }

        query += ") AND (invoice_initial.active_status IS NULL OR invoice_initial.active_status='Y') \n\
                AND (payment.active_status IS NULL OR payment.active_status='Y') \n\
                GROUP BY invoice.id \n\
            ) hutang \n\
            LEFT JOIN person ON hutang.orang_id=person.id \n\
            LEFT JOIN company ON person.perusahaan_id=company.id \n\
            WHERE (person.active_status='Y' AND person.tipe='VENDOR'"

        if (data['penjualId'] != 0) {
            query += " AND person.id=${data['penjualId'].longValue()}"
        }

        query += ") AND company.active_status='Y' "

        if (data.containsKey('invoice.no')) {
            query += "AND LOWER(invoice_no) LIKE LOWER('%${data['invoice.no']}%') "
        }

        if (data.containsKey('invoice.tanggal')) {
            query += "AND DATE(invoice_tanggal) = DATE('${Date.parse('MMM dd, yyyy HH:mm:ss a', data['invoice.tanggal']).format('yyyy-MM-dd')}') "
        }

        if (data.containsKey('total')) {
            query += "AND total_temp <= ${data['total']} "
        }

        if (data.containsKey('bayar')) {
            query += "AND bayar_temp <= ${data['bayar']} "
        }

        if (data.containsKey('sisa')) {
            query += "AND sisa <= ${data['sisa']} "
        }

        query += "AND (total - bayar <> 0)"

        def hutangs = db.rows(query)
        return !hutangs.empty ? db.rows(query).size() : 0
    }

    def countBayar(params, data) {
        return Pembayaran.withCriteria {
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
                        eq('activeStatus', 'Y')
                    }
                    eq('tipe', 'VENDOR')
                    eq('activeStatus', 'Y')
                }
                if (data.containsKey('invoice.no')) {
                    ilike('no', "%${data['invoice.no']}%")
                }
                eq('activeStatus', 'Y')
            }
            rekening {
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('tanggal')) {
                def dateTemp = Date.parse('MMM dd, yyyy HH:mm:ss a', data['tanggal'])
                between('tanggal', dateTemp, dateTemp + 1)
            }

            if (data.containsKey('jumlah')) {
                le('jumlah', new BigDecimal(data['jumlah']))
            }

            if (data.containsKey('deskripsi')) {
                ilike('deskripsi', "%${data['deskripsi']}%")
            }

            eq('activeStatus', 'Y')
        }.size()
    }
}
