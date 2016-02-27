package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.Satuan
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class SatuanService {
    
    def listAll() {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order('kode', 'asc')
            projections {
                property('id', 'id')
                property('kode', 'kode')
                property('deskripsi', 'deskripsi')
            }
        }
    }
    
    def list(params) {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('kode', 'kode')
                property('deskripsi', 'deskripsi')
            }
        }
    }
    
    def listKode() {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order('kode', 'asc')
            projections {
                property('id', 'id')
                property('kode', 'kode')
            }
        }
    }

    def save(data) {
        def satuan = new Satuan()
        satuan.kode = data.kode
        satuan.deskripsi = data.deskripsi
        satuan.activeStatus = 'Y'
        
        def response = [:]
        if (satuan.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = satuan.id
        } else {
            response['message'] = 'failed'
            response['error'] = satuan.errors.allErrors.code
        }
        return response
    }
    
    def get(id) {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('kode', 'kode')
                property('deskripsi', 'deskripsi')
            }
        }[0]
    }
    
    def update(id, data) {
        def satuan = Satuan.get(id)
        satuan.kode = data.kode
        satuan.deskripsi = data.deskripsi
        
        def response = [:]
        if (satuan.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = satuan.id
        } else {
            response['message'] = 'failed'
            response['error'] = satuan.errors.allErrors.code
        }
        return response
    }
    
    def delete(id) {
        def satuan = Satuan.get(id)
        satuan.activeStatus = 'N'
        
        def response = [:]
        if (satuan.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def checkKode(kode) {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('kode', kode, [ignoreCase: true])
        }.size()
    }
    
    def count(params) {
        return Satuan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
        }.size()
    }
}
