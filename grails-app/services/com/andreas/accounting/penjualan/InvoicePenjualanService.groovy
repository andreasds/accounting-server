package com.andreas.accounting.penjualan

import com.andreas.accounting.util.Invoice
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class InvoicePenjualanService {
    
    def listAll() {
        
    }

    def list(params, data) {
        
    }

    def save(data) {
        
    }

    def get(id) {
        
    }

    def update(id, data) {
        
    }

    def delete(id) {
        
    }

    def checkNo(no, pemilikId) {
        return Invoice.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            invoice {
                perusahaan {
                    idEq(pemilikId)
                }
                orang {
                    eq('tipe', 'CUSTOMER')
                }
                eq('no', no, [ignoreCase: true])
            }
        }.size()
    }

    def count(params, data) {
        
    }
}
