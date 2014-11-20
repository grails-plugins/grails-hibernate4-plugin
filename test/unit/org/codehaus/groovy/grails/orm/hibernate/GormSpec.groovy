package org.codehaus.groovy.grails.orm.hibernate

import grails.util.Metadata

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.cfg.ConfigurationHelper
import org.codehaus.groovy.grails.commons.spring.GrailsRuntimeConfigurator
import org.codehaus.groovy.grails.commons.spring.WebRuntimeSpringConfiguration
import org.codehaus.groovy.grails.plugins.DefaultGrailsPlugin
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.MockGrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.support.MockApplicationContext
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.StaticMessageSource
import org.springframework.mock.web.MockServletContext
import org.springframework.orm.hibernate4.SessionHolder
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.Log4jConfigurer
import org.springframework.web.context.request.RequestContextHolder

import spock.lang.Specification

abstract class GormSpec extends Specification {

	MockApplicationContext parentCtx
	ApplicationContext applicationContext
	GrailsApplication grailsApplication
	GroovyClassLoader gcl = new GroovyClassLoader(getClass().classLoader)
	GrailsPluginManager mockManager
	SessionFactory sessionFactory
	Session session

	def setup() {
		Log4jConfigurer.initLogging("test/unit/log4j.properties")

		ConfigurationHelper.clearCachedConfigs()

		ExpandoMetaClass.enableGlobally()

		GroovySystem.metaClassRegistry.metaClassCreationHandle = new ExpandoMetaClassCreationHandle()

		configureDataSource()

		parentCtx = new MockApplicationContext()

		grailsApplication = new DefaultGrailsApplication(gcl.getLoadedClasses(), gcl)
		grailsApplication.metadata[Metadata.APPLICATION_NAME] = getClass().name

		def dependentPlugins = configurePlugins()

		initializeApplication()

		parentCtx.registerMockBean("messageSource", new StaticMessageSource())

		def springConfig = new WebRuntimeSpringConfiguration(parentCtx, gcl)
		springConfig.servletContext = new MockServletContext()

		doWithRuntimeConfiguration dependentPlugins, springConfig

		grailsApplication.setMainContext(springConfig.getUnrefreshedApplicationContext())
		applicationContext = springConfig.getApplicationContext()
		dependentPlugins*.doWithApplicationContext(applicationContext)

		mockManager.applicationContext = applicationContext
		mockManager.doDynamicMethods()

		registerHibernateSession()
	}

	def cleanup() {
		unbindSessionFactory()
		sessionFactory = null

		GroovySystem.stopThreadedReferenceManager()

		try {
			TransactionSynchronizationManager.clear()
		}
		catch(e) {
			// means it is not active, ignore
		}
		try {
			getClass().classLoader.loadClass("net.sf.ehcache.CacheManager").getInstance()?.shutdown()
		}
		catch(e) {
			// means there is no cache, ignore
		}
		gcl = null
		grailsApplication = null
		mockManager = null
		applicationContext?.close()
		applicationContext = null

		ExpandoMetaClass.disableGlobally()
		RequestContextHolder.setRequestAttributes(null)
		PluginManagerHolder.setPluginManager(null)
	}

	protected void unregisterHibernateSession() {
		unbindSessionFactory()
	}

	protected void unbindSessionFactory(SessionFactory sf = sessionFactory) {
		if (sf && TransactionSynchronizationManager.hasResource(sf)) {
			Session s = TransactionSynchronizationManager.getResource(sf).session
			TransactionSynchronizationManager.unbindResource sf
			try {
				s.close()
			}
			catch (e) {
				// ignored
			}
		}

		for (metadata in sf?.allClassMetadata?.values()) {
			GroovySystem.getMetaClassRegistry().removeMetaClass metadata.getMappedClass()
		}
	}

	protected void registerHibernateSession() {
		sessionFactory = applicationContext.getBean(GrailsRuntimeConfigurator.SESSION_FACTORY_BEAN)
		bindSessionFactory sessionFactory
		session = sessionFactory.currentSession
	}

	protected void bindSessionFactory(SessionFactory sessionFactory) {
		if (!TransactionSynchronizationManager.hasResource(sessionFactory)) {
			TransactionSynchronizationManager.bindResource(sessionFactory,
				new SessionHolder(sessionFactory.openSession()))
		}
	}

	protected void initializeApplication() {
		grailsApplication.initialise()
		domainClasses.each { dc -> grailsApplication.addArtefact 'Domain', dc }
		grailsApplication.setApplicationContext(applicationContext)
		parentCtx.registerMockBean(GrailsApplication.APPLICATION_ID, grailsApplication)
	}

	protected void doWithRuntimeConfiguration(dependentPlugins, springConfig) {
		dependentPlugins*.doWithRuntimeConfiguration(springConfig)
	}

	protected List configurePlugins() {
		mockManager = new MockGrailsPluginManager(grailsApplication)

		parentCtx.registerMockBean("pluginManager", mockManager)

		def dependantPluginClasses = []
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.CoreGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.CodecsGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.datasource.DataSourceGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.i18n.I18nGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.ServletsGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.mapping.UrlMappingsGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.ControllersGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.GroovyPagesGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.mimes.MimeTypesGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.web.filters.FiltersGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.converters.ConvertersGrailsPlugin")
		dependantPluginClasses << gcl.loadClass("org.codehaus.groovy.grails.plugins.services.ServicesGrailsPlugin")
		dependantPluginClasses << MockHibernateGrailsPlugin

		def dependentPlugins = dependantPluginClasses.collect { new DefaultGrailsPlugin(it, grailsApplication) }

		dependentPlugins.each { mockManager.registerMockPlugin(it); it.manager = mockManager }
		mockManager.doArtefactConfiguration()
		dependentPlugins
	}

	protected void configureDataSource() {
		gcl.parseClass('''
dataSource {
	pooled = true
	driverClassName = "org.h2.Driver"
	username = "sa"
	password = ""
	dbCreate = "create-drop"
	url = "jdbc:h2:mem:grailsIntTestDB"
}
hibernate {
	cache.use_second_level_cache=true
	cache.use_query_cache=true
	cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory'
}
''', "DataSource")
	}

	abstract List getDomainClasses()
}
