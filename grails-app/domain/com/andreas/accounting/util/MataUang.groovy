package com.andreas.accounting.util

class MataUang {
    
    String nama
    String kode

    static constraints = {
        kode unique: true
    }
    
    static mapping = {
        table 'currency'
        version false
        sort nama: 'asc'
        
        nama length: 40
        kode length: 5
    }
}
