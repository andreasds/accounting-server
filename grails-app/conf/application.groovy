grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.andreas.accounting.auth.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.andreas.accounting.auth.UserRole'
grails.plugin.springsecurity.authority.className = 'com.andreas.accounting.auth.Role'
grails.plugin.springsecurity.securityConfigType = 'Annotation'

grails.plugin.springsecurity.active=true
grails.plugin.springsecurity.rest.token.storage.useGorm = true
grails.plugin.springsecurity.rest.token.storage.gorm.tokenDomainClassName = 'com.andreas.accounting.auth.AuthenticationToken'
grails.plugin.springsecurity.rest.token.rendering.usernamePropertyName = 'username'
grails.plugin.springsecurity.rest.token.rendering.tokenPropertyName = 'token'
grails.plugin.springsecurity.rest.token.rendering.authoritiesPropertyName = 'roles'
grails.plugin.springsecurity.rest.token.validation.useBearerToken = false
grails.plugin.springsecurity.rest.token.validation.headerName = 'X-Auth-Token'
grails.plugin.springsecurity.rest.logout.endpointUrl = '/api/logout'

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
    [pattern: '/**',                    access: ['ROLE_ADMIN', 'ROLE_USER']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
    [pattern: '/api/**',                filters: 'JOINED_FILTERS'],
    [pattern: '/**',                    filters: 'JOINED_FILTERS']
]