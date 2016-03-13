package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.saldoawal.ProdukAwal
import com.andreas.accounting.util.MataUang
import grails.transaction.Transactional
import org.hibernate.criterion.CriteriaSpecification

@Transactional
class ProdukAwalService {
    
    def listAll() {
        return ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            produk {
                kategoriProduk {
                    eq('activeStatus', 'Y')
                }
                satuan {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            order('jumlah', 'asc')
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('hargaBeli', 'hargaBeli')
                property('rate', 'rate')
                property('tanggal', 'tanggal')
                property('perusahaan', 'perusahaan')
                property('produk', 'produk')
                property('mataUang', 'mataUang')
            }
        }
    }
    
    def list(params) {
        def produkAwals = ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            produk {
                kategoriProduk {
                    eq('activeStatus', 'Y')
                }
                satuan {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            order(params.sort, params.order)
            maxResults(params.max)
            firstResult(params.offset)
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('hargaBeli', 'hargaBeli')
                property('rate', 'rate')
                property('perusahaan', 'perusahaan')
                property('produk', 'produk')
                property('mataUang', 'mataUang')
            }
        }
        
        if (!produkAwals.empty) {
            produkAwals.each { produkAwal ->
                def perusahaan = [:]
                perusahaan['id'] = produkAwal['perusahaan']['id']
                perusahaan['nama'] = produkAwal['perusahaan']['nama']
                produkAwal['perusahaan'] = perusahaan
                
                def produk = [:]
                produk['id'] = produkAwal['produk']['id']
                produk['indeks'] = produkAwal['produk']['indeks']
                produk['deskripsi'] = produkAwal['produk']['deskripsi']
                
                def kategoriProduk = [:]
                kategoriProduk['id'] = produkAwal['produk']['kategoriProduk']['id']
                kategoriProduk['kode'] = produkAwal['produk']['kategoriProduk']['kode']
                produk['kategoriProduk'] = kategoriProduk
                produkAwal['produk'] = produk
                
                def mataUang = [:]
                mataUang['id'] = produkAwal['mataUang']['id']
                mataUang['kode'] = produkAwal['mataUang']['kode']
                produkAwal['mataUang'] = mataUang
            }
        }
        return produkAwals
    }
    
    def save(data) {
        def produkAwal = new ProdukAwal()
        produkAwal.jumlah = data.jumlah
        produkAwal.hargaBeli = data.hargaBeli
        produkAwal.rate = data.rate
        produkAwal.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        produkAwal.activeStatus = 'Y'
        produkAwal.perusahaan = Perusahaan.get(data.perusahaan.id)
        produkAwal.produk = Produk.get(data.produk.id)
        produkAwal.mataUang = MataUang.get(data.mataUang.id)
        
        def response = [:]
        if (produkAwal.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = produkAwal.id
        } else {
            response['message'] = 'failed'
            response['error'] = produkAwal.errors.allErrors.code
        }
        return response
    }
    
    def get(id) {
        def produkAwal = ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            produk {
                kategoriProduk {
                    eq('activeStatus', 'Y')
                }
                satuan {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            idEq(id)
            eq('activeStatus', 'Y')
            projections {
                property('id', 'id')
                property('jumlah', 'jumlah')
                property('hargaBeli', 'hargaBeli')
                property('rate', 'rate')
                property('tanggal', 'tanggal')
                property('perusahaan', 'perusahaan')
                property('produk', 'produk')
                property('mataUang', 'mataUang')
            }
        }
        
        if (!produkAwal.empty) {
            produkAwal = produkAwal[0]
            
            def perusahaan = [:]
            perusahaan['id'] = produkAwal['perusahaan']['id']
            perusahaan['nama'] = produkAwal['perusahaan']['nama']
            produkAwal['perusahaan'] = perusahaan
            
            def produk = [:]
            produk['id'] = produkAwal['produk']['id']
            produk['indeks'] = produkAwal['produk']['indeks']
            produk['deskripsi'] = produkAwal['produk']['deskripsi']
                
            def kategoriProduk = [:]
            kategoriProduk['id'] = produkAwal['produk']['kategoriProduk']['id']
            kategoriProduk['kode'] = produkAwal['produk']['kategoriProduk']['kode']
            produk['kategoriProduk'] = kategoriProduk
            produkAwal['produk'] = produk
                
            def mataUang = [:]
            mataUang['id'] = produkAwal['mataUang']['id']
            mataUang['nama'] = produkAwal['mataUang']['nama']
            mataUang['kode'] = produkAwal['mataUang']['kode']
            produkAwal['mataUang'] = mataUang
        }
        return produkAwal
    }
    
    def update(id, data) {
        def produkAwal = ProdukAwal.get(id)
        produkAwal.jumlah = data.jumlah
        produkAwal.hargaBeli = data.hargaBeli
        produkAwal.rate = data.rate
        produkAwal.tanggal = Date.parse('MMM dd, yyyy HH:mm:ss a', data.tanggal)
        produkAwal.perusahaan = Perusahaan.get(data.perusahaan.id)
        produkAwal.produk = Produk.get(data.produk.id)
        produkAwal.mataUang = MataUang.get(data.mataUang.id)
            
        def response = [:]
        if (produkAwal.save(flush: true)) {
            response['message'] = 'succeed'
            response['id'] = produkAwal.id
        } else {
            response['message'] = 'failed'
            response['error'] = produkAwal.errors.allErrors.code
        }
        return response
    }
        
    def delete(id) {
        def produkAwal = ProdukAwal.get(id)
        produkAwal.activeStatus = 'N'
            
        def response = [:]
        if (produkAwal.save(flush: true)) {
            response['message'] = 'succeed'
        } else {
            response['message'] = 'failed'
        }
        return response
    }
    
    def checkProduk(produkId, perusahaanId) {
        return ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                idEq(perusahaanId)
            }
            produk {
                idEq(produkId)
            }
        }.size()
    }
    
    def getTotal(perusahaanId) {
        def total = ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                if (perusahaanId != 0) {
                    idEq(perusahaanId)
                }
                eq('activeStatus', 'Y')
            }
            produk {
                kategoriProduk {
                    eq('activeStatus', 'Y')
                }
                satuan {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            projections {
                sum('total', 'total')
            }
        }[0]['total']
        return total != null ? total : 0
    }
        
    def count(params) {
        return ProdukAwal.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            perusahaan {
                eq('activeStatus', 'Y')
            }
            produk {
                kategoriProduk {
                    eq('activeStatus', 'Y')
                }
                satuan {
                    eq('activeStatus', 'Y')
                }
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
        }.size()
    }
}
