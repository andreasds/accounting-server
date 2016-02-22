package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarproduk.Produk
import grails.converters.JSON
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class AwalPersediaanService {
    
    def listAll() {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
                order('kode', 'asc')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            gt('jumlahAwal', 0)
            eq('activeStatus', 'Y')
            order('deskripsi', 'asc')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('jumlahAwal', 'jumlahAwal')
                property('hargaBeliAwal', 'hargaBeliAwal')
                property('kategoriProduk', 'kategoriProduk')
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
            gt('jumlahAwal', 0)
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('jumlahAwal', 'jumlahAwal')
                property('hargaBeliAwal', 'hargaBeliAwal')
                property('kategoriProduk', 'kategoriProduk')
            }
        }
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
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('jumlahAwal', 'jumlahAwal')
                property('hargaBeliAwal', 'hargaBeliAwal')
                property('kategoriProduk', 'kategoriProduk')
            }
        }[0]
    }

    def update(id, data) {
        def produk = Produk.get(id)
        produk.jumlahAwal = data.jumlahAwal
        produk.hargaBeliAwal = data.hargaBeliAwal
        
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
        produk.jumlahAwal = 0
        produk.hargaBeliAwal = 0
        
        def response = [:]
        if (produk.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def checkProduk(id) {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            idEq(id)
            gt('jumlahAwal', 0)
        }.size()
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
            gt('jumlahAwal', 0)
            eq('activeStatus', 'Y')
        }.size()
    }
}
