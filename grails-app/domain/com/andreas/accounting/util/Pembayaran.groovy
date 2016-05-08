package com.andreas.accounting.util

class Pembayaran {

    Date tanggal
    BigDecimal jumlah
    BigDecimal rate
    String deskripsi
    String activeStatus

    BigDecimal total

    static belongsTo = [
        invoice: Invoice,
        rekening: Rekening,
        mataUang: MataUang
    ]

    static constraints = {
        deskripsi nullable: true
    }

    static mapping = {
        table 'payment'
        version false
        sort tanggal: 'desc'
        invoice sort: 'no', order: 'asc'
        rekening sort: 'nama', order: 'asc'
        mataUang sort: 'nama', order: 'asc'
        deskripsi length: 500
        activeStatus length: 1

        total formula: 'jumlah * rate'
    }
}
