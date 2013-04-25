package org.codehaus.groovy.grails.orm.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.Reference;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

/**
 * Dummy SessionFactory implementation useful for unit testing.
 *
 * @author Dmitriy Kopylenko
 * @since 0.5
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SessionFactoryAdapter implements SessionFactory {

	private static final long serialVersionUID = 1;

	public Reference getReference() {
		return null;
	}

	public void close() {
		// do nothing
	}

	public void evict(Class arg0, Serializable arg1) {
		// do nothing
	}

	public void evict(Class arg0) {
		// do nothing
	}

	public void evictCollection(String arg0, Serializable arg1) {
		// do nothing
	}

	public void evictCollection(String arg0) {
		// do nothing
	}

	public void evictEntity(String arg0, Serializable arg1) {
		// do nothing
	}

	public void evictEntity(String arg0) {
		// do nothing
	}

	public void evictQueries() {
		// do nothing
	}

	public void evictQueries(String arg0) {
		// do nothing
	}

	public Map getAllClassMetadata() {
		return null;
	}

	public Map getAllCollectionMetadata() {
		return null;
	}

	public ClassMetadata getClassMetadata(Class arg0) {
		return null;
	}

	public ClassMetadata getClassMetadata(String arg0) {
		return null;
	}

	public CollectionMetadata getCollectionMetadata(String arg0) {
		return null;
	}

	public Session getCurrentSession() {
		return null;
	}

	public Set getDefinedFilterNames() {
		return null;
	}

	public FilterDefinition getFilterDefinition(String arg0) {
		return null;
	}

	public boolean containsFetchProfileDefinition(String name) {
		return false;
	}

	public TypeHelper getTypeHelper() {
		return null;
	}

	public Statistics getStatistics() {
		return null;
	}

	public boolean isClosed() {
		return false;
	}

	public Cache getCache() {
		return null;
	}

	public Session openSession() {
		return null;
	}

	public StatelessSession openStatelessSession() {
		return null;
	}

	public StatelessSession openStatelessSession(Connection arg0) {
		return null;
	}

	public SessionFactoryOptions getSessionFactoryOptions() {
		return null;
	}

	public SessionBuilder withOptions() {
		return null;
	}

	public StatelessSessionBuilder withStatelessOptions() {
		return null;
	}
}
