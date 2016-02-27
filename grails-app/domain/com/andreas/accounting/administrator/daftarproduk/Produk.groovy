package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.Satuan

class Produk {

    int indeks
    String deskripsi
    String activeStatus
    
    static belongsTo = [
        kategoriProduk: KategoriProduk,
        satuan: Satuan
    ]

    static constraints = {
        indeks unique: 'kategoriProduk'
    }
    
    static mapping = {
        table 'item'
        version false
        sort deskripsi: 'asc'
        kategoriProduk sort: 'nama', order: 'asc'
        satuan sort: 'kode', order: 'asc'
        
        deskripsi length: 150
        activeStatus length: 1
    }
}
