package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.KategoriProduk
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.daftarproduk.Satuan
import grails.converters.JSON
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class ProdukService {
    
    def listAll() {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('kategoriProduk', 'kategoriProduk')
                property('satuan', 'satuan')
            }
        }
    }
    
    def list(params) {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
                order('kode', params.order)
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('kategoriProduk', 'kategoriProduk')
                property('satuan', 'satuan')
            }
        }
    }
    
    def listKode() {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('kategoriProduk', 'kategoriProduk')
            }
        }
    }

    def save(data) {
        def produk = new Produk()
        produk.indeks = getLastIndex(data.kategoriProduk.id) + 1
        produk.deskripsi = data.deskripsi
        produk.jumlahAwal = 0
        produk.hargaBeliAwal = 0
        produk.activeStatus = 'Y'
        produk.kategoriProduk = KategoriProduk.get(data.kategoriProduk.id)
        produk.satuan = Satuan.get(data.satuan.id)
        
        def response = [:]
        if (produk.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = produk.id
        } else {
            response['message'] = 'failed'
            response['error'] = produk.errors.allErrors.code
        }
        return response
    }
    
    def getLastIndex(kategoriProdukId) {
        def indeks = Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('id', kategoriProdukId.longValue())
            }
            order('indeks', 'desc')
            maxResults(1)
            projections {
                property('indeks', 'indeks')
            }
        }
        return indeks.size() > 0 ? indeks[0]['indeks'] : 0
    }
    
    def get(id) {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('id', id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('kategoriProduk', 'kategoriProduk')
                property('satuan', 'satuan')
            }
        }[0]
    }
    
    def update(id, data) {
        def produk = Produk.get(id)
        if (produk.kategoriProduk.id != data.kategoriProduk.id) {
            produk.indeks = getLastIndex(data.kategoriProduk.id) + 1
        } else {
            produk.indeks = data.indeks
        }
        produk.deskripsi = data.deskripsi
        produk.jumlahAwal = data.jumlahAwal
        produk.hargaBeliAwal = data.hargaBeliAwal
        produk.kategoriProduk = KategoriProduk.get(data.kategoriProduk.id)
        produk.satuan = Satuan.get(data.satuan.id)
        
        def response = [:]
        if (produk.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = produk.id
        } else {
            response['message'] = 'failed'
            response['error'] = produk.errors.allErrors.code
        }
        return response
    }
    
    def delete(id) {
        def produk = Produk.get(id)
        produk.activeStatus = 'N'
        
        def response = [:]
        if (produk.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def count(params) {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
        }.size()
    }
}
