package org.codehaus.groovy.grails.orm.hibernate

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Version

import org.hibernate.annotations.IndexColumn

@Entity
@Table(name="faq_section")
class FaqSection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id

	@Version
	Long version

	String title

	@OneToMany(cascade = [CascadeType.ALL], targetEntity = FaqElement)
	@JoinColumn(name = "section_id", nullable = false)
	@IndexColumn(name = "pos", base = 0)
	List elements
}
