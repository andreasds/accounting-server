package com.andreas.accounting.util

import com.andreas.accounting.util.Menu
import grails.transaction.Transactional

@Transactional
class MenuService {
    
    def springSecurityService

    def authorizedMenu() {
        def result = []
        def roles = springSecurityService.currentUser.getAuthorities()
        def parentsMenu = Menu.withCriteria {
            'in'('role', roles)
            isNull('parent')
            order('nama', 'asc')
        }
        
        parentsMenu.each { parentMenu ->
            def menu = [:]
            menu['nama'] = parentMenu['nama']
            menu['path'] = parentMenu['path']
            menu['icon'] = parentMenu['icon']
            menu['subMenu'] = getChildMenu(parentMenu, roles)
            result.add(menu)
        }
        
        return result
    }
    
    def getChildMenu(parentMenu, roles) {
        def result = []
        def childsMenu = Menu.withCriteria {
            'in'('role', roles)
            eq('parent', parentMenu)
            order('nama', 'asc')
        }
        
        if (childsMenu.empty) {
            return null
        } else {
            childsMenu.each { childMenu ->
                def menu = [:]
                menu['nama'] = childMenu['nama']
                menu['path'] = childMenu['path']
                menu['icon'] = childMenu['icon']
                menu['subMenu'] = getChildMenu(childMenu, roles)
                result.add(menu)
            }
        }
        
        return result
    }
}
