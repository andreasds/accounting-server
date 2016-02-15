package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.KategoriProduk
import grails.converters.JSON
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class KategoriProdukService {
    
    def listAll() {
        return KategoriProduk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }
    }
    
    def list(params) {
        return KategoriProduk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }
    }
    
    def listNama() {
        return KategoriProduk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
            }
        }
    }

    def save(data) {
        def kategoriProduk = new KategoriProduk()
        kategoriProduk.nama = data.nama
        kategoriProduk.kode = data.kode
        kategoriProduk.activeStatus = 'Y'
        
        def response = [:]
        if (kategoriProduk.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = kategoriProduk.id
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def get(id) {
        return KategoriProduk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('id', id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }[0]
    }
    
    def update(id, data) {
        def kategoriProduk = KategoriProduk.get(id)
        kategoriProduk.nama = data.nama
        kategoriProduk.kode = data.kode
        
        def response = [:]
        if (kategoriProduk.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = kategoriProduk.id
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def delete(id) {
        def kategoriProduk = KategoriProduk.get(id)
        kategoriProduk.activeStatus = 'N'
        
        def response = [:]
        if (kategoriProduk.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def count(params) {
        return KategoriProduk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
        }.size()
    }
}
