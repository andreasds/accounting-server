package com.andreas.accounting.util

import com.andreas.accounting.administrator.daftarproduk.Produk
import java.math.BigDecimal

class ProdukInvoice {

    BigDecimal jumlah
    BigDecimal harga
    BigDecimal rate

    BigDecimal total

    static belongsTo = [
        invoice: Invoice,
        produk: Produk
    ]

    static constraints = {
        produk unique: 'invoice'
    }

    static mapping = {
        table 'item_invoice'
        version false
        sort total: 'desc'
        invoice sort: 'no', order: 'asc'
        produk sort: 'deskripsi', order: 'asc'

        total formula: 'jumlah * harga * rate'
    }
}
