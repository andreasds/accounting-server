package com.andreas.accounting.administrator.daftarproduk

class KategoriProduk {

    String nama
    String kode
    String activeStatus

    static constraints = {
        nama unique: true
        kode unique: true
    }
    
    static mapping = {
        table 'item_category'
        
        nama length: 50
        kode length: 5
        activeStatus length: 1
    }
}
