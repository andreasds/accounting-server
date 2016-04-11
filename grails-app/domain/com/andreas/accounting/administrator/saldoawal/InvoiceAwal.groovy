package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.util.Invoice
import com.andreas.accounting.util.MataUang
import java.math.BigDecimal

class InvoiceAwal {

    BigDecimal jumlah
    BigDecimal rate
    String activeStatus

    BigDecimal total

    static belongsTo = [
        invoice: Invoice,
        mataUang: MataUang
    ]

    static constraints = {
        invoice unique: true
    }

    static mapping = {
        table 'invoice_initial'
        version false
        invoice sort: 'no', order: 'asc'
        mataUang sort: 'nama', order: 'asc'

        activeStatus length: 1

        total formula: 'jumlah * rate'
    }
}
