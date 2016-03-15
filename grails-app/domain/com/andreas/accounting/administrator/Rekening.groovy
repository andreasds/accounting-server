package com.andreas.accounting.administrator

class Rekening {

    String nama
    String deskripsi
    String activeStatus

    static constraints = {
        nama unique: true
        deskripsi nullable: true
    }

    static mapping = {
        table 'cash_account'
        version false
        sort nama: 'asc'

        nama length: 100
        deskripsi length: 500
        activeStatus length: 1
    }
}
