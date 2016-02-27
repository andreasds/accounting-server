package com.andreas.accounting.administrator

import com.andreas.accounting.administrator.Rekening
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class RekeningService {
    
    def listAll() {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order('nama', 'asc')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('deskripsi', 'deskripsi')
            }
        }
    }
    
    def list(params) {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('deskripsi', 'deskripsi')
            }
        }
    }
    
    def listNama() {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order('nama', 'asc')
            projections {
                property('id', 'id')
                property('nama', 'nama')
            }
        }
    }

    def save(data) {
        def rekening = new Rekening()
        rekening.nama = data.nama
        rekening.deskripsi = data.deskripsi
        rekening.saldoAwal = 0
        rekening.activeStatus = 'Y'
        
        def response = [:]
        if (rekening.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = rekening.id
        } else {
            response['message'] = 'failed'
            response['error'] = rekening.errors.allErrors.code
        }
        return response
    }
    
    def get(id) {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('deskripsi', 'deskripsi')
            }
        }[0]
    }
    
    def update(id, data) {
        def rekening = Rekening.get(id)
        rekening.nama = data.nama
        rekening.deskripsi = data.deskripsi
        
        def response = [:]
        if (rekening.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = rekening.id
        } else {
            response['message'] = 'failed'
            response['error'] = rekening.errors.allErrors.code
        }
        return response
    }
    
    def delete(id) {
        def rekening = Rekening.get(id)
        rekening.activeStatus = 'N'
        
        def response = [:]
        if (rekening.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def checkNama(nama) {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('nama', nama, [ignoreCase: true])
        }.size()
    }
    
    def count(params) {
        return Rekening.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
        }.size()
    }
}
