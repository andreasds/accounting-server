package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.Satuan

class Produk {

    String kode
    String deskripsi
    int jumlahAwal
    int hargaBeliAwal
    String activeStatus
    
    static belongsTo = [satuan: Satuan]

    static constraints = {
        
    }
    
    static mapping = {
        table 'item'
        
        kode length: 20
        deskripsi length: 150
        activeStatus length: 1
    }
}
