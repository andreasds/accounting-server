package com.andreas.accounting.util

import com.andreas.accounting.util.MataUang
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class MataUangService {
    
    def listAll() {
        return MataUang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            order('nama', 'asc')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }
    }
    
    def get(id) {
        return MataUang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            idEq(id)
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }[0]
    }
    
    def getIDR() {
        return MataUang.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            eq('kode', 'IDR')
            projections {
                property('id', 'id')
                property('nama', 'nama')
                property('kode', 'kode')
            }
        }[0]
    }
    
    def init() {
        MataUang AUD = MataUang.findByKode('AUD') ?: new MataUang (
            nama: 'Australian Dollar',
            kode: 'AUD'
        ).save(flush: true)
        
        MataUang BGN = MataUang.findByKode('BGN') ?: new MataUang (
            nama: 'Bulgarian Lev',
            kode: 'BGN'
        ).save(flush: true)
        
        MataUang BRL = MataUang.findByKode('BRL') ?: new MataUang (
            nama: 'Brazilian Real',
            kode: 'BRL'
        ).save(flush: true)
        
        MataUang CAD = MataUang.findByKode('CAD') ?: new MataUang (
            nama: 'Canadian Dollar',
            kode: 'CAD'
        ).save(flush: true)
        
        MataUang CHF = MataUang.findByKode('CHF') ?: new MataUang (
            nama: 'Swiss Franc',
            kode: 'CHF'
        ).save(flush: true)
        
        MataUang CNY = MataUang.findByKode('CNY') ?: new MataUang (
            nama: 'Chinese Yuan',
            kode: 'CNY'
        ).save(flush: true)
        
        MataUang CZK = MataUang.findByKode('CZK') ?: new MataUang (
            nama: 'Czech Republic Koruna',
            kode: 'CZK'
        ).save(flush: true)
        
        MataUang DKK = MataUang.findByKode('DKK') ?: new MataUang (
            nama: 'Danish Krone',
            kode: 'DKK'
        ).save(flush: true)
        
        MataUang EUR = MataUang.findByKode('EUR') ?: new MataUang (
            nama: 'Euro',
            kode: 'EUR'
        ).save(flush: true)
        
        MataUang GBP = MataUang.findByKode('GBP') ?: new MataUang (
            nama: 'British Pound',
            kode: 'GBP'
        ).save(flush: true)
        
        MataUang HKD = MataUang.findByKode('HKD') ?: new MataUang (
            nama: 'Hong Kong Dollar',
            kode: 'HKD'
        ).save(flush: true)
        
        MataUang HRK = MataUang.findByKode('HRK') ?: new MataUang (
            nama: 'Croatian Kuna',
            kode: 'HRK'
        ).save(flush: true)
        
        MataUang HUF = MataUang.findByKode('HUF') ?: new MataUang (
            nama: 'Hungarian Forint',
            kode: 'HUF'
        ).save(flush: true)
        
        MataUang IDR = MataUang.findByKode('IDR') ?: new MataUang (
            nama: 'Indonesian Rupiah',
            kode: 'IDR'
        ).save(flush: true)
        
        MataUang ILS = MataUang.findByKode('ILS') ?: new MataUang (
            nama: 'Israeli Shekel',
            kode: 'ILS'
        ).save(flush: true)
        
        MataUang INR = MataUang.findByKode('INR') ?: new MataUang (
            nama: 'Indian Rupee',
            kode: 'INR'
        ).save(flush: true)
        
        MataUang JPY = MataUang.findByKode('JPY') ?: new MataUang (
            nama: 'Japanese Yen',
            kode: 'JPY'
        ).save(flush: true)
        
        MataUang KRW = MataUang.findByKode('KRW') ?: new MataUang (
            nama: 'South Korean Won',
            kode: 'KRW'
        ).save(flush: true)
        
        MataUang MXN = MataUang.findByKode('MXN') ?: new MataUang (
            nama: 'Mexican Peso',
            kode: 'MXN'
        ).save(flush: true)
        
        MataUang MYR = MataUang.findByKode('MYR') ?: new MataUang (
            nama: 'Malaysian Ringgit',
            kode: 'MYR'
        ).save(flush: true)
        
        MataUang NOK = MataUang.findByKode('NOK') ?: new MataUang (
            nama: 'Norwegian Krone',
            kode: 'NOK'
        ).save(flush: true)
        
        MataUang NZD = MataUang.findByKode('NZD') ?: new MataUang (
            nama: 'New Zealand Dollar',
            kode: 'NZD'
        ).save(flush: true)
        
        MataUang PHP = MataUang.findByKode('PHP') ?: new MataUang (
            nama: 'Philippine Peso',
            kode: 'PHP'
        ).save(flush: true)
        
        MataUang PLN = MataUang.findByKode('PLN') ?: new MataUang (
            nama: 'Polish Zloty',
            kode: 'PLN'
        ).save(flush: true)
        
        MataUang RON = MataUang.findByKode('RON') ?: new MataUang (
            nama: 'Romanian Leu',
            kode: 'RON'
        ).save(flush: true)
        
        MataUang RUB = MataUang.findByKode('RUB') ?: new MataUang (
            nama: 'Russian Ruble',
            kode: 'RUB'
        ).save(flush: true)
        
        MataUang SEK = MataUang.findByKode('SEK') ?: new MataUang (
            nama: 'Swedish Krona',
            kode: 'SEK'
        ).save(flush: true)
        
        MataUang SGD = MataUang.findByKode('SGD') ?: new MataUang (
            nama: 'Singapore Dollar',
            kode: 'SGD'
        ).save(flush: true)
        
        MataUang THB = MataUang.findByKode('THB') ?: new MataUang (
            nama: 'Thai Bath',
            kode: 'THB'
        ).save(flush: true)
        
        MataUang TRY = MataUang.findByKode('TRY') ?: new MataUang (
            nama: 'Turkish Lira',
            kode: 'TRY'
        ).save(flush: true)
        
        MataUang USD = MataUang.findByKode('USD') ?: new MataUang (
            nama: 'US Dollar',
            kode: 'USD'
        ).save(flush: true)
        
        MataUang ZAR = MataUang.findByKode('ZAR') ?: new MataUang (
            nama: 'South African Rand',
            kode: 'ZAR'
        ).save(flush: true)
    }
}
