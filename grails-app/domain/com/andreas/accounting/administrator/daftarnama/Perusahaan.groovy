package com.andreas.accounting.administrator.daftarnama

class Perusahaan {

    String nama
    String alamat
    String kota
    boolean pemilik
    String activeStatus

    static constraints = {
        nama unique: true
        alamat nullable: true
        kota nullable: true
    }
    
    static mapping = {
        table 'company'
        version false
        sort nama: 'asc'
        
        nama length: 100
        alamat length: 500
        kota length: 20
        activeStatus length: 1
    }
}
