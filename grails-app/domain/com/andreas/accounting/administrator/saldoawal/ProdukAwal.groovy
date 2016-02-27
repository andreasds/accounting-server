package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.util.MataUang
import java.math.BigDecimal
import java.util.Date

class ProdukAwal {
    
    int jumlah
    BigDecimal hargaBeli
    BigDecimal rate
    Date tanggal
    String activeStatus
    
    BigDecimal total
    
    static belongsTo = [
        perusahaan: Perusahaan,
        produk: Produk,
        mataUang: MataUang
    ]

    static constraints = {
        produk unique: 'perusahaan'
    }
    
    static mapping = {
        table 'item_initial'
        version false
        sort jumlah: 'asc'
        perusahaan sort: 'nama', order: 'asc'
        produk sort: 'deskripsi', order: 'asc'
        mataUang sort: 'nama', order: 'asc'
        
        tanggal sqlType: 'date'
        activeStatus length: 1
        
        total formula: 'jumlah * harga_beli * rate'
    }
}
