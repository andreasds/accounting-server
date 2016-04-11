package com.andreas.accounting.administrator.daftarproduk

import com.andreas.accounting.administrator.daftarproduk.KategoriProduk
import com.andreas.accounting.administrator.daftarproduk.Produk
import com.andreas.accounting.administrator.daftarproduk.Satuan
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
            order('deskripsi', 'asc')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('kategoriProduk', 'kategoriProduk')
                property('satuan', 'satuan')
            }
        }
    }

    def list(params, data) {
        def produks = Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                if (data.containsKey('kategoriProduk.nama')) {
                    ilike('nama', "%${data['kategoriProduk.nama']}%")
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'kategoriProduk.nama') {
                    order('nama', params.order)
                }
            }
            satuan {
                if (data.containsKey('satuan.kode')) {
                    ilike('kode', "%${data['satuan.kode']}%")
                }

                eq('activeStatus', 'Y')

                if (params.sort == 'satuan.kode') {
                    order('kode', params.order)
                }
            }

            if (data.containsKey('kode')) {
                ilike('kode', "%${data['kode']}%")
            }

            if (data.containsKey('deskripsi')) {
                ilike('deskripsi', "%${data['deskripsi']}%")
            }

            eq('activeStatus', 'Y')
            if (params.sort == 'deskripsi') {
                order(params.sort, params.order)
            }
            kategoriProduk {
                order('kode', params.order)
            }
            order('indeks', params.order)
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

        if (!produks.empty) {
            produks.each { produk ->
                def kategoriProduk = [:]
                kategoriProduk['id'] = produk['kategoriProduk']['id']
                kategoriProduk['nama'] = produk['kategoriProduk']['nama']
                kategoriProduk['kode'] = produk['kategoriProduk']['kode']
                produk['kategoriProduk'] = kategoriProduk

                def satuan = [:]
                satuan['id'] = produk['satuan']['id']
                satuan['kode'] = produk['satuan']['kode']
                produk['satuan'] = satuan
            }
        }
        return produks
    }

    def listKode() {
        def produks = Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                eq('activeStatus', 'Y')
            }
            satuan {
                eq('activeStatus', 'Y')
            }
            eq('activeStatus', 'Y')
            order('deskripsi', 'asc')
            projections {
                property('id', 'id')
                property('indeks', 'indeks')
                property('deskripsi', 'deskripsi')
                property('kategoriProduk', 'kategoriProduk')
            }
        }

        if (!produks.empty) {
            produks.each { produk ->
                def kategoriProduk = [:]
                kategoriProduk['id'] = produk['kategoriProduk']['id']
                kategoriProduk['kode'] = produk['kategoriProduk']['kode']
                produk['kategoriProduk'] = kategoriProduk
            }
        }
        return produks
    }

    def save(data) {
        def produk = new Produk()
        produk.indeks = getLastIndex(data.kategoriProduk.id) + 1
        produk.deskripsi = data.deskripsi
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
            projections {
                max('indeks', 'indeks')
            }
        }
        return indeks[0]['indeks'] != null ? indeks[0]['indeks'] : 0
    }

    def get(id) {
        def produk = Produk.withCriteria {
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
                property('kategoriProduk', 'kategoriProduk')
                property('satuan', 'satuan')
            }
        }

        if (!produk.empty) {
            produk = produk[0]

            def kategoriProduk = [:]
            kategoriProduk['id'] = produk['kategoriProduk']['id']
            kategoriProduk['nama'] = produk['kategoriProduk']['nama']
            kategoriProduk['kode'] = produk['kategoriProduk']['kode']
            produk['kategoriProduk'] = kategoriProduk

            def satuan = [:]
            satuan['id'] = produk['satuan']['id']
            satuan['kode'] = produk['satuan']['kode']
            produk['satuan'] = satuan
        }
        return produk
    }

    def update(id, data) {
        def produk = Produk.get(id)
        if (produk.kategoriProduk.id != data.kategoriProduk.id) {
            produk.indeks = getLastIndex(data.kategoriProduk.id) + 1
        } else {
            produk.indeks = data.indeks
        }
        produk.deskripsi = data.deskripsi
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

    def count(params, data) {
        return Produk.withCriteria {
            resultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
            kategoriProduk {
                if (data.containsKey('kategoriProduk.nama')) {
                    ilike('nama', "%${data['kategoriProduk.nama']}%")
                }

                if (data.containsKey('kategoriProduk.kode')) {
                    ilike('kode', "%${data['kategoriProduk.kode']}%")
                }

                eq('activeStatus', 'Y')
            }
            satuan {
                if (data.containsKey('satuan.kode')) {
                    ilike('kode', "%${data['satuan.kode']}%")
                }
                eq('activeStatus', 'Y')
            }

            if (data.containsKey('kode')) {
                ilike('kode', "%${data['kode']}%")
            }

            if (data.containsKey('deskripsi')) {
                ilike('deskripsi', "%${data['deskripsi']}%")
            }

            eq('activeStatus', 'Y')
        }.size()
    }
}
