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

        String datastoreVersion = '3.0.5.BUILD-SNAPSHOT'
        String hibernateVersion = '4.3.4.Final'

        compile "org.grails:grails-datastore-core:$datastoreVersion",
                "org.grails:grails-datastore-gorm:$datastoreVersion",
                "org.grails:grails-datastore-gorm-hibernate4:$datastoreVersion",
                "org.grails:grails-datastore-simple:$datastoreVersion"

        compile "javax.validation:validation-api:1.1.0.Final" 
		runtime 'cglib:cglib:2.2.2'

		runtime "org.hibernate:hibernate-ehcache:$hibernateVersion"
		runtime "org.hibernate:hibernate-validator:5.0.3.Final"
        runtime "net.sf.ehcache:ehcache-core:2.4.8"

		runtime 'org.jboss.logging:jboss-logging:3.1.0.GA', {
			excludes 'jboss-logmanager', 'log4j', 'slf4j-api'
		}
	}

	plugins {
        build(':release:3.0.1', ':rest-client-builder:1.0.3') {
			export = false
		}

		test ':scaffolding:1.0.0', {
			export = false
		}
	}
}
