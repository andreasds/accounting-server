package com.andreas.accounting.administrator

class Satuan {

    String kode
    String nama
    String deskripsi
    String activeStatus

    static constraints = {
        nama nullable: true
        deskripsi nullable: true
    }
    
    static mapping = {
        table 'unit'
        
        kode length: 10
        nama length: 20
        deskripsi length: 500
        activeStatus length: 1
    }
}
