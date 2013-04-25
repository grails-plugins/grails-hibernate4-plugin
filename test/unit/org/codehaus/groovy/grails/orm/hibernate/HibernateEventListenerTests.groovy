package org.codehaus.groovy.grails.orm.hibernate

import org.codehaus.groovy.grails.plugins.DefaultGrailsPlugin
import org.hibernate.event.service.spi.EventListenerRegistry
import org.hibernate.event.spi.EventType
import org.hibernate.event.spi.PostDeleteEvent
import org.hibernate.event.spi.PostDeleteEventListener
import org.hibernate.event.spi.PostInsertEvent
import org.hibernate.event.spi.PostInsertEventListener
import org.hibernate.event.spi.SaveOrUpdateEvent
import org.hibernate.event.spi.SaveOrUpdateEventListener

/**
 * @author Burt Beckwith
 */
class HibernateEventListenerTests extends AbstractGrailsHibernateTests {

	private plugin

	protected void afterPluginInitialization() {
		plugin = new DefaultGrailsPlugin(EventListenerGrailsPlugin, ga)
		mockManager.registerMockPlugin plugin
		plugin.manager = mockManager
	}

	protected void doWithRuntimeConfiguration(dependentPlugins, springConfig) {
		super.doWithRuntimeConfiguration dependentPlugins, springConfig
		plugin.doWithRuntimeConfiguration springConfig
	}

	void testDoRuntimeConfiguration() {

		EventListenerRegistry listenerRegistry = sessionFactory.serviceRegistry.getService(EventListenerRegistry)

		assertTrue listenerRegistry.getEventListenerGroup(EventType.POST_INSERT).listeners().any { it instanceof TestAuditListener }
		assertTrue listenerRegistry.getEventListenerGroup(EventType.POST_DELETE).listeners().any { it instanceof TestAuditListener }
		assertTrue listenerRegistry.getEventListenerGroup(EventType.SAVE_UPDATE).listeners().any { it instanceof TestAuditListener }
		assertFalse listenerRegistry.getEventListenerGroup(EventType.POST_UPDATE).listeners().any { it instanceof TestAuditListener }
	}
}

class EventListenerGrailsPlugin {
	def version = 1
	def doWithSpring = {
		testAuditListener(TestAuditListener)
		hibernateEventListeners(HibernateEventListeners) {
			listenerMap = [
				'post-insert': testAuditListener,
				'post-delete': testAuditListener,
				'save-update': testAuditListener]
		}
	}
}

class TestAuditListener implements PostInsertEventListener, PostDeleteEventListener, SaveOrUpdateEventListener {
	void onPostInsert(PostInsertEvent event) {}
	void onPostDelete(PostDeleteEvent event) {}
	void onSaveOrUpdate(SaveOrUpdateEvent event) {}
}
