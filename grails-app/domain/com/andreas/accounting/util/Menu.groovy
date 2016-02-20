package com.andreas.accounting.util

import com.andreas.accounting.auth.Role

class Menu {

    String nama
    String path
    String icon
    Menu parent
    
    static belongsTo = [
        role: Role
    ]
    
    static constraints = {
        parent blank: true, nullable: true
    }
    
    static mapping = {
        version false
    }
}
