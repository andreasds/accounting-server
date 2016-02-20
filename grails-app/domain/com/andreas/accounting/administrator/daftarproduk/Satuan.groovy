package com.andreas.accounting.administrator.daftarproduk

class Satuan {

    String kode
    String deskripsi
    String activeStatus

    static constraints = {
        kode unique: true
        deskripsi nullable: true
    }
    
    static mapping = {
        table 'unit'
        version false
        
        kode length: 10
        deskripsi length: 500
        activeStatus length: 1
    }
}
