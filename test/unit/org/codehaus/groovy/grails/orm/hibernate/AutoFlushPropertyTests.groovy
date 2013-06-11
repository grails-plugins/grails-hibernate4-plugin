package org.codehaus.groovy.grails.orm.hibernate

import grails.persistence.Entity

import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.FlushEvent
import org.hibernate.event.spi.FlushEventListener
import org.hibernate.service.ServiceRegistry

class AutoFlushPropertyTests extends AbstractGrailsHibernateTests {

	private int flushCount = 0
	private band

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

	protected void onSetUp() {
		band = new AutoFlushBand(name: 'Tool')
	}

	void testFlushIsDisabledByDefault() {
		assertNotNull band.save()
		band.merge()
		band.delete()
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testFlushPropertyTrue() {
		setAutoFlush true

		assertNotNull band.save()
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge()
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete()
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testFlushPropertyFalse() {
		setAutoFlush false

		assertNotNull band.save()
		band.merge()
		band.delete()
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testTrueFlushArgumentOverridesFalsePropertySetting() {
		setAutoFlush true

		assert band.save(flush: true)
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge(flush: true)
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete(flush: true)
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testFalseFlushArgumentOverridesTruePropertySetting() {
		setAutoFlush true

		assertNotNull band.save(flush: false)
		band.merge(flush: false)
		band.delete(flush: false)
		assertEquals 'Wrong flush count', 0, flushCount
	}

	void testMapWithoutFlushEntryRespectsTruePropertySetting() {
		setAutoFlush true

		assertNotNull band.save([:])
		assertEquals 'Wrong flush count after save', 1, flushCount
		band.merge([:])
		assertEquals 'Wrong flush count after merge', 2, flushCount
		band.delete([:])
		assertEquals 'Wrong flush count after delete', 3, flushCount
	}

	void testMapWithoutFlushEntryRespectsFalsePropertySetting() {
		setAutoFlush false

		assertNotNull band.save([:])
		band.merge([:])
		band.delete([:])
		assertEquals 'Wrong flush count', 0, flushCount
	}

	private void setAutoFlush(boolean auto) {
		def config = AutoFlushBand.currentGormInstanceApi().config
		if (config == null) {
			config = [:]
			AutoFlushBand.currentGormInstanceApi().config = config
		}
		config.autoFlush = auto

		ga.config.grails.gorm.autoFlush = auto
		ga.configChanged()
	}
}

@Entity
class AutoFlushBand {
	String name
}
