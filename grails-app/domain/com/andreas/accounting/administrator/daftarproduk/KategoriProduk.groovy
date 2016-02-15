package com.andreas.accounting.administrator.daftarproduk

class KategoriProduk {

    String nama
    String kode
    String activeStatus

    static constraints = {
        
    }
    
    static mapping = {
        table 'item_category'
        
        nama length: 50
        kode length: 5
        activeStatus length: 1
    }
}
