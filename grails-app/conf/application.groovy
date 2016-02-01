grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.andreas.accounting.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.andreas.accounting.auth.UserRole'
grails.plugin.springsecurity.authority.className = 'com.andreas.accounting.auth.Role'
grails.plugin.springsecurity.securityConfigType = 'Annotation'

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    [pattern: '/',                      access: ['permitAll']],
    [pattern: '/error',                 access: ['permitAll']],
    [pattern: '/index',                 access: ['permitAll']],
    [pattern: '/index.gsp',             access: ['permitAll']],
    [pattern: '/shutdown',              access: ['permitAll']],
    [pattern: '/assets/**',             access: ['permitAll']],
    [pattern: '/**/js/**',              access: ['permitAll']],
    [pattern: '/**/css/**',             access: ['permitAll']],
    [pattern: '/**/images/**',          access: ['permitAll']],
    [pattern: '/**/favicon.ico',        access: ['permitAll']],
    [pattern: '/admin/**',              access: 'ROLE_ADMIN'],
    [pattern: '/**/**/**',              access: ['ROLE_ADMIN', 'ROLE_USER']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
    [pattern: '/**',                    filters: 'JOINED_FILTERS']
]