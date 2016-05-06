package com.andreas.accounting.administrator.saldoawal

import com.andreas.accounting.administrator.daftarnama.Perusahaan
import com.andreas.accounting.util.Rekening
import java.math.BigDecimal

class RekeningAwal {

    BigDecimal saldo
    String activeStatus

    static belongsTo = [
        perusahaan: Perusahaan,
        rekening: Rekening
    ]

    static constraints = {
        rekening unique: 'perusahaan'
    }

    static mapping = {
        table 'cash_account_initial'
        version false
        sort saldo: 'asc'

        activeStatus length: 1
    }
}
