package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.Satuan

class Produk {

    int indeks
    String deskripsi
    int jumlahAwal
    int hargaBeliAwal
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
        
        deskripsi length: 150
        activeStatus length: 1
    }
}
