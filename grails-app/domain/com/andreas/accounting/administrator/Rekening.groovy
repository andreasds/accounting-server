package com.andreas.accounting.administrator

class Rekening {
    
    String nama
    String deskripsi
    int saldoAwal
    String activeStatus

    static constraints = {
        nama unique: true
        deskripsi nullable: true
    }
    
    static mapping = {
        table 'cash_account'
        version false
        
        nama length: 100
        deskripsi length: 500
        activeStatus length: 1
    }
}
