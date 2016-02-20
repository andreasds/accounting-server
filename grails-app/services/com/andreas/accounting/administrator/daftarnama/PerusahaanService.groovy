package com.andreas.accounting.administrator.daftarnama

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import grails.converters.JSON
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class PerusahaanService {
    
    def listAll() {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('alamat', 'alamat')
                property('kota', 'kota')
            }
        }
    }
    
    def list(params) {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('alamat', 'alamat')
                property('kota', 'kota')
            }
        }
    }
    
    def listNama() {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('pemilik', false)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
            }
        }
    }
    
    def listNamaPemilik() {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('pemilik', true)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
            }
        }
    }

    def save(data) {
        def perusahaan = new Perusahaan()
        perusahaan.nama = data.nama
        perusahaan.alamat = data.alamat
        perusahaan.kota = data.kota
        perusahaan.pemilik = data.pemilik
        perusahaan.activeStatus = 'Y'
        
        def response = [:]
        if (perusahaan.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = perusahaan.id
        } else {
            response['message'] = 'failed'
            response['error'] = perusahaan.errors.allErrors.code
        }
        return response
    }
    
    def get(id) {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('alamat', 'alamat')
                property('kota', 'kota')
            }
        }[0]
    }
    
    def update(id, data) {
        def perusahaan = Perusahaan.get(id)
        perusahaan.nama = data.nama
        perusahaan.alamat = data.alamat
        perusahaan.kota = data.kota
        perusahaan.pemilik = data.pemilik
        
        def response = [:]
        if (perusahaan.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = perusahaan.id
        } else {
            response['message'] = 'failed'
            response['error'] = perusahaan.errors.allErrors.code
        }
        return response
    }
    
    def delete(id) {
        def perusahaan = Perusahaan.get(id)
        perusahaan.activeStatus = 'N'
        
        def response = [:]
        if (perusahaan.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def checkNama(nama) {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('nama', nama, [ignoreCase: true])
        }.size()
    }
    
    def count(params) {
        return Perusahaan.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('activeStatus', 'Y')
        }.size()
    }
}
