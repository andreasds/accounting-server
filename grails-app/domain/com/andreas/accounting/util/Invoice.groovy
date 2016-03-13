package com.andreas.accounting.util

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarnama.Orang
import java.util.Date

class Invoice {
    
    String no
    Date tanggal
    String activeStatus
    
    static belongsTo = [
        perusahaan: Perusahaan,
        orang: Orang
    ]

    static constraints = {
        no unique: 'perusahaan'
    }
    
    static mapping = {
        table 'invoice'
        version false
        sort tanggal: 'asc'
        perusahaan sort: 'nama', order: 'asc'
        orang sort: 'nama', order: 'asc'
        
        no length: 20
        tanggal sqlType: 'date'
        activeStatus length: 1
    }
}
