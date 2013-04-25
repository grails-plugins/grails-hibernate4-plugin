package org.codehaus.groovy.grails.orm.hibernate

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.persistence.Version

@Entity
@Table(name="faq_element")
class FaqElement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

	@Version
	Long version

	String question
	String answer

	@ManyToOne
	@JoinColumn(name = "section_id", nullable = false, updatable = false, insertable = false)
	FaqSection section
}
