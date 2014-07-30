if(System.getenv('TRAVIS_BRANCH')) {
    grails.project.repos.grailsCentral.username = System.getenv("GRAILS_CENTRAL_USERNAME")
    grails.project.repos.grailsCentral.password = System.getenv("GRAILS_CENTRAL_PASSWORD")    
}

grails.project.work.dir = 'target'

forkConfig = false
grails.project.fork = [
	test:    forkConfig, // configure settings for the test-app JVM
	run:     forkConfig, // configure settings for the run-app JVM
	war:     forkConfig, // configure settings for the run-war JVM
	console: forkConfig, // configure settings for the Swing console JVM
	compile: forkConfig  // configure settings for compilation
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

	inherits "global"
	log "warn"

	repositories {
        mavenLocal()
		grailsCentral()
		mavenRepo "http://repo.grails.org/grails/core"
	}

	dependencies {

        String datastoreVersion = '3.1.2.RELEASE'
        String hibernateVersion = '4.3.5.Final'

        compile "org.grails:grails-datastore-core:$datastoreVersion",
                "org.grails:grails-datastore-gorm:$datastoreVersion",
                "org.grails:grails-datastore-gorm-hibernate4:$datastoreVersion",
                "org.grails:grails-datastore-simple:$datastoreVersion", {
                exclude group:'org.springframework', name:'spring-context'
                exclude group:'org.springframework', name:'spring-core'
                exclude group:'org.springframework', name:'spring-beans'
                exclude group:'org.grails', name:'grails-bootstrap'
                exclude group:'org.grails', name:'grails-core'                
                exclude group:'org.grails', name:'grails-async'                
                exclude 'javax.transaction:jta'
        }

        compile "javax.validation:validation-api:1.1.0.Final" 

        runtime "org.hibernate:hibernate-validator:5.0.3.Final"
        runtime "org.hibernate:hibernate-ehcache:$hibernateVersion", {
            exclude group: 'net.sf.ehcache', name: 'ehcache-core'
            exclude group: 'org.jboss.logging', name:'jboss-logging-annotations'
        }
        runtime "net.sf.ehcache:ehcache-core:2.4.8"

		runtime 'org.jboss.logging:jboss-logging:3.1.0.GA', {
			excludes 'jboss-logmanager', 'log4j', 'slf4j-api'
		}
	}

	plugins {
        build(':release:3.0.1', ':rest-client-builder:2.0.3') {
			export = false
		}

		test ':scaffolding:1.0.0', {
			export = false
		}
	}
}
