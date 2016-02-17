package com.andreas.accounting.administrator.daftarnama

import com.andreas.accounting.administrator.daftarnama.Perusahaan

class Orang {

    String tipe
    String nama
    String telepon
    String hp
    String activeStatus
    
    static belongsTo = [perusahaan: Perusahaan]

    static constraints = {
        nama unique: 'perusahaan'
        telepon nullable: true
        hp nullable: true
    }
    
    static mapping = {
        table 'person'
        
        tipe length: 10
        nama length: 100
        telepon length: 15
        hp length: 15
        activeStatus length: 1
    }
}
