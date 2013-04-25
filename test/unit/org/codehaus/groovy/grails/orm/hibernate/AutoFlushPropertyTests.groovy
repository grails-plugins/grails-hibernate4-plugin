package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.FlushEvent
import org.hibernate.event.spi.FlushEventListener
import org.hibernate.service.ServiceRegistry

class AutoFlushPropertyTests extends AbstractGrailsHibernateTests {

	private int flushCount = 0

	private FlushEventListener listener = new FlushEventListener() {
		void onFlush(FlushEvent e) {
			++flushCount
		}
	}

	@Override
	protected void registerHibernateSession() {
		super.registerHibernateSession()
		flushCount = 0
		ServiceRegistry serviceRegistry = sessionFactory.serviceRegistry
		EventListenerRegistry listenerRegistry = serviceRegistry.getService(EventListenerRegistry)
		listenerRegistry.appendListeners EventType.FLUSH, listener
	}

	protected getDomainClasses() {
		[AutoFlushBand]
	}

	protected void onTearDown() {
		ConfigurationHolder.config = null
	}

	void testFlushIsDisabledByDefault() {
		def band = createBand('Tool')
		assertNotNull band.save()
		band.merge()
		band.delete()
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testFlushPropertyTrue() {
		ga.config.grails.gorm.autoFlush = true
		ga.configChanged()

		def band = createBand('Tool')
		assertNotNull band.save()
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge()
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete()
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testFlushPropertyFalse() {
		ga.config.grails.gorm.autoFlush = false
		ga.configChanged()

		def band = createBand('Tool')
		assertNotNull band.save()
		band.merge()
		band.delete()
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testTrueFlushArgumentOverridesFalsePropertySetting() {
		ga.config.grails.gorm.autoFlush = true
		ga.configChanged()

		def band = createBand('Tool')
		assert band.save(flush: true)
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge(flush: true)
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete(flush: true)
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testFalseFlushArgumentOverridesTruePropertySetting() {
		ga.config.grails.gorm.autoFlush = true
		ga.configChanged()

		def band = createBand('Tool')
		assertNotNull band.save(flush: false)
		band.merge(flush: false)
		band.delete(flush: false)
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testMapWithoutFlushEntryRespectsTruePropertySetting() {
		ga.config.grails.gorm.autoFlush = true
		ga.configChanged()

		def band = createBand('Tool')
		assertNotNull band.save([:])
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge([:])
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete([:])
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testMapWithoutFlushEntryRespectsFalsePropertySetting() {
		ga.config.grails.gorm.autoFlush = false
		ga.configChanged()

		def band = createBand('Tool')
		assertNotNull band.save([:])
		band.merge([:])
		band.delete([:])
		assertEquals 'Wrong flush count', 0, flushCount
	}

	private createBand(name) {
		def band = ga.getDomainClass(AutoFlushBand.name).newInstance()
		band.name = name
		band
	}
}

@Entity
class AutoFlushBand {
	String name
}
