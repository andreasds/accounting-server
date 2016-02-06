package com.andreas.accounting.util

import com.andreas.accounting.auth.Role
import com.andreas.accounting.auth.User
import com.andreas.accounting.auth.UserRole
import com.andreas.accounting.util.Menu
import grails.transaction.Transactional

@Transactional
class AdministratorService {
    
    def init() {
        // ########## user ##########
        User admin = User.findByUsername('admin') ?: new User('admin', 'admin').save(failOnError: true)
        User lilik = User.findByUsername('lilik') ?: new User('lilik', 'lilik').save(failOnError: true)
        
        // ########## role ##########
        Role roleAdmin = Role.findByAuthority('ROLE_ADMIN') ?: new Role('ROLE_ADMIN').save(failOnError: true)
        Role roleUser = Role.findByAuthority('ROLE_USER') ?: new Role('ROLE_USER').save(failOnError: true)
        
        // ########## role user ##########
        UserRole.findByUserAndRole(admin, roleAdmin) ?: UserRole.create(admin, roleAdmin, true)
        UserRole.findByUserAndRole(admin, roleUser) ?: UserRole.create(admin, roleUser, true)
        UserRole.findByUserAndRole(lilik, roleUser) ?: UserRole.create(lilik, roleUser, true)
        
        // ########## menu ##########
        // ===== data administrator =====
        Menu menuAdministrator = Menu.findByNama('Data Administrator') ?: new Menu (
            nama: 'Data Administrator',
            path: '#',
            icon: 'fa fa-database',
            parent: null,
            role: roleUser
        ).save(flush: true)
        
        // daftar nama
        Menu menuDaftarNama = Menu.findByNama('Daftar Nama') ?: new Menu (
            nama: 'Data Nama',
            path: '#',
            icon: 'fa fa-users',
            parent: menuAdministrator,
            role: roleAdmin
        ).save(flush: true)
        Menu menuPembeli = Menu.findByNama('Pembeli') ?: new Menu (
            nama: 'Pembeli',
            path: '/modules/administrator/daftar-nama/pembeli/list.xhtml',
            icon: 'fa fa-user-plus',
            parent: menuDaftarNama,
            role: roleAdmin
        ).save(flush: true)
        Menu menuPenjual = Menu.findByNama('Penjual') ?: new Menu (
            nama: 'Penjual',
            path: '/modules/administrator/daftar-nama/penjual/list.xhtml',
            icon: 'fa fa-user',
            parent: menuDaftarNama,
            role: roleAdmin
        ).save(flush: true)
        Menu menuPerusahaan = Menu.findByNama('Perusahaan') ?: new Menu (
            nama: 'Perusahaan',
            path: '/modules/administrator/daftar-nama/perusahaan/list.xhtml',
            icon: 'fa fa-building',
            parent: menuDaftarNama,
            role: roleAdmin
        ).save(flush: true)
        
        // data produk
        Menu menuProduk = Menu.findByNama('Produk') ?: new Menu (
            nama: 'Produk',
            path: '/modules/administrator/produk/list.xhtml',
            icon: 'fa fa-product-hunt',
            parent: menuAdministrator,
            role: roleUser
        ).save(flush: true)
        
        // data rekening
        Menu menuDataRekening = Menu.findByNama('Rekening') ?: new Menu (
            nama: 'Rekening',
            path: '/modules/administrator/rekening/list.xhtml',
            icon: 'fa fa-university',
            parent: menuAdministrator,
            role: roleAdmin
        ).save(flush: true)
        
        // saldo awal
        Menu menuSaldoAwal = Menu.findByNama('Saldo Awal') ?: new Menu (
            nama: 'Saldo Awal',
            path: '#',
            icon: 'fa fa-balance-scale',
            parent: menuAdministrator,
            role: roleAdmin
        ).save(flush: true)
        Menu menuSaldoAwalHutang = Menu.findByNama('Saldo Hutang') ?: new Menu (
            nama: 'Saldo Hutang',
            path: '/modules/administrator/saldo-awal/hutang/list.xhtml',
            icon: 'fa fa-minus',
            parent: menuSaldoAwal,
            role: roleAdmin
        ).save(flush: true)
        Menu menuSaldoAwalPersediaan = Menu.findByNama('Saldo Persediaan') ?: new Menu (
            nama: 'Saldo Persediaan',
            path: '/modules/administrator/saldo-awal/persediaan/list.xhtml',
            icon: 'fa fa-shopping-basket',
            parent: menuSaldoAwal,
            role: roleAdmin
        ).save(flush: true)
        Menu menuSaldoAwalPiutang = Menu.findByNama('Saldo Piutang') ?: new Menu (
            nama: 'Saldo Piutang',
            path: '/modules/administrator/saldo-awal/piutang/list.xhtml',
            icon: 'fa fa-plus',
            parent: menuSaldoAwal,
            role: roleAdmin
        ).save(flush: true)
        Menu menuSaldoAwalRekening = Menu.findByNama('Saldo Rekening') ?: new Menu (
            nama: 'Saldo Rekening',
            path: '/modules/administrator/saldo-awal/rekening/list.xhtml',
            icon: 'fa fa-money',
            parent: menuSaldoAwal,
            role: roleAdmin
        ).save(flush: true)
        
        // data satuan pengukuran
        Menu menuSatuanPengukuran = Menu.findByNama('Satuan Pengukuran') ?: new Menu (
            nama: 'Satuan Pengukuran',
            path: '/modules/administrator/satuan-pengukuran/list.xhtml',
            icon: 'fa fa-tag',
            parent: menuAdministrator,
            role: roleUser
        ).save(flush: true)
        
        // ===== dashboard =====
        Menu menuDashboard = Menu.findByNama('Dashboard') ?: new Menu (
            nama: 'Dashboard',
            path: '/home.xhtml',
            icon: 'fa fa-tachometer',
            parent: null,
            role: roleUser
        ).save(flush: true)
        
        // ===== pembelian =====
        Menu menuPembelian = Menu.findByNama('Pembelian') ?: new Menu (
            nama: 'Pembelian',
            path: '#',
            icon: 'fa fa-shopping-cart',
            parent: null,
            role: roleUser
        ).save(flush: true)
        
        // hutang
        Menu menuHutang = Menu.findByNama('Hutang') ?: new Menu (
            nama: 'Hutang',
            path: '/modules/pembelian/hutang/list.xhtml',
            icon: 'fa fa-minus',
            parent: menuPembelian,
            role: roleUser
        ).save(flush: true)
        
        // invoice pembelian
        Menu menuInvoicePembelian = Menu.findByNama('Invoice Pembelian') ?: new Menu (
            nama: 'Invoice Pembelian',
            path: '/modules/pembelian/invoice/list.xhtml',
            icon: 'fa fa-file-text',
            parent: menuPembelian,
            role: roleUser
        ).save(flush: true)
        
        // retur pembelian
        Menu menuReturPembelian = Menu.findByNama('Retur Pembelian') ?: new Menu (
            nama: 'Retur Pembelian',
            path: '/modules/pembelian/retur/list.xhtml',
            icon: 'fa fa-reply',
            parent: menuPembelian,
            role: roleUser
        ).save(flush: true)
        
        // ===== penjualan =====
        Menu menuPenjualan = Menu.findByNama('Penjualan') ?: new Menu (
            nama: 'Penjualan',
            path: '#',
            icon: 'fa fa-truck',
            parent: null,
            role: roleUser
        ).save(flush: true)
        
        // invoice penjualan
        Menu menuInvoicePenjualan = Menu.findByNama('Invoice Penjualan') ?: new Menu (
            nama: 'Invoice Penjualan',
            path: '/modules/penjualan/invoice/list.xhtml',
            icon: 'fa fa-file-text',
            parent: menuPenjualan,
            role: roleUser
        ).save(flush: true)
        
        // piutang
        Menu menuPiutang = Menu.findByNama('Piutang') ?: new Menu (
            nama: 'Piutang',
            path: '/modules/penjualan/piutang/list.xhtml',
            icon: 'fa fa-plus',
            parent: menuPenjualan,
            role: roleUser
        ).save(flush: true)
        
        // retur penjualan
        Menu menuReturPenjualan = Menu.findByNama('Retur Penjualan') ?: new Menu (
            nama: 'Retur Penjualan',
            path: '/modules/penjualan/retur/list.xhtml',
            icon: 'fa fa-reply',
            parent: menuPenjualan,
            role: roleUser
        ).save(flush: true)
        
        // ===== rekening =====
        Menu menuRekening = Menu.findByNama('Rekening') ?: new Menu (
            nama: 'Rekening',
            path: '#',
            icon: 'fa fa-money',
            parent: null,
            role: roleUser
        ).save(flush: true)
        
        // kas keluar
        Menu menuKasKeluar = Menu.findByNama('Kas Keluar') ?: new Menu (
            nama: 'Kas Keluar',
            path: '/modules/rekening/kas-keluar/list.xhtml',
            icon: 'fa fa-minus-square',
            parent: menuRekening,
            role: roleUser
        ).save(flush: true)
        
        // kas masuk
        Menu menuKasMasuk = Menu.findByNama('Kas Masuk') ?: new Menu (
            nama: 'Kas Masuk',
            path: '/modules/rekening/kas-masuk/list.xhtml',
            icon: 'fa fa-plus-square',
            parent: menuRekening,
            role: roleUser
        ).save(flush: true)
        
        // transfer
        Menu menuTransfer = Menu.findByNama('Transfer') ?: new Menu (
            nama: 'Transfer',
            path: '/modules/rekening/transfer/list.xhtml',
            icon: 'fa fa-usd',
            parent: menuRekening,
            role: roleUser
        ).save(flush: true)
    }

    def serviceMethod() {

    }
}
