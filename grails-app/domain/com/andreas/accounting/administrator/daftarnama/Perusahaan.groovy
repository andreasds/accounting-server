package com.andreas.accounting.administrator.daftarnama

class Perusahaan {

    String nama
    String alamat
    String kota
    String activeStatus

    static constraints = {
        alamat nullable: true
        kota nullable: true
    }
    
    static mapping = {
        table 'company'
        
        nama length: 100
        alamat length: 500
        kota length: 20
        activeStatus length: 1
    }
}