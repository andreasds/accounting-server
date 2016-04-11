package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.Satuan

class Produk {

    int indeks
    String deskripsi
    String activeStatus

    String kode

    static belongsTo = [
        kategoriProduk: KategoriProduk,
        satuan: Satuan
    ]

    static constraints = {
        indeks unique: 'kategoriProduk'
    }

    static mapping = {
        table 'item'
        version false
        sort deskripsi: 'asc'
        kategoriProduk sort: 'nama', order: 'asc'
        satuan sort: 'kode', order: 'asc'

        deskripsi length: 150
        activeStatus length: 1

        kode formula: "concat(\n\
                        (select kp.kode \n\
                        from item_category kp \n\
                        where kategori_produk_id = kp.id), \n\
                        '-', \n\
                        lpad(indeks, 5, '0'))"
    }
}
