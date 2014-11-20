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
		mavenRepo "https://repo.grails.org/grails/core"
        mavenRepo "https://repo.grails.org/grails/libs-snapshots-local"
	}

	dependencies {

        String datastoreVersion = '3.1.4.BUILD-SNAPSHOT'
        String hibernateVersion = '4.3.7.Final'

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
        // Required by Hibernate Validator
        // Java EE 6 require that EL 2.2 be provided.
        // In addition to all Java EE 6 application servers,
        // Tomcat 7 and Jetty 7 also include it.
        // Pretty much every (not terribly old) servlet container
        // and application server includes it, so it's okay to be provided.
        // If it's scoped compile, it will conflict with what the container
        // provides and cause classloading problems.
        provided 'javax.el:javax.el-api:2.2.4'

        runtime "org.hibernate:hibernate-validator:5.1.3.Final"
        runtime "org.hibernate:hibernate-ehcache:$hibernateVersion", {
            exclude group: 'net.sf.ehcache', name: 'ehcache-core'
            exclude group: 'org.jboss.logging', name:'jboss-logging-annotations'
        }
       runtime "net.sf.ehcache:ehcache:2.9.0"

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
