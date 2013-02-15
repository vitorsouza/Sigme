package br.com.engenhodesoftware.sigme.secretariat.domain;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import br.com.engenhodesoftware.util.ejb3.persistence.PersistentObjectSupport;

/**
 * Abstract domain class that represents an addressee of a mailing list. Addressees are spiritists that are associated
 * (either directly or through their attendance of an institution) to mailing lists, which means they will receive all
 * mailing sent to the specified list.
 * 
 * Subclasses of this class should specify different types of addressee, for instance: a specific spiritist, all
 * spiritists attending a given institution, all spiritists attending an institution of a given regional, etc.
 * 
 * @author Vitor E. Silva Souza (vitorsouza@gmail.com)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "class", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("M")
public abstract class MailingAddressee extends PersistentObjectSupport implements Comparable<MailingAddressee> {
	/** Serialization id. */
	private static final long serialVersionUID = 1L;

	/** Mailing list to which this addressee belongs. */
	@ManyToOne(cascade = CascadeType.MERGE)
	protected MailingList mailingList;

	/** Type of addressee (normal, copy or blind copy). */
	@Basic
	@NotNull
	protected MailingAddresseeType type;

	/** Getter for mailingList. */
	public MailingList getMailingList() {
		return mailingList;
	}

	/** Setter for mailingList. */
	public void setMailingList(MailingList mailingList) {
		this.mailingList = mailingList;
	}

	/** Getter for type. */
	public MailingAddresseeType getType() {
		return type;
	}

	/** Setter for type. */
	public void setType(MailingAddresseeType type) {
		this.type = type;
	}

	/**
	 * Indicates if the addressee is scoped. When scoped, the scope of the addressee should also be indicated. The scope
	 * is implemented by the MailingAddresseeScope enumeration and indicates if the message should be sent to all related
	 * spiritists, to active spiritists only or to inactive spiritists only.
	 * 
	 * @return <code>true</code> if the addressee is scoped, <code>false</code> otherwise.
	 * @see br.com.engenhodesoftware.sigme.secretariat.domain.MailingAddresseeScope
	 * @see br.com.engenhodesoftware.sigme.secretariat.domain.ScopedMailingAddressee
	 */
	public boolean isScoped() {
		// By default, addressees aren't scoped. ScopedMailingAddressee overrides this.
		return false;
	}

	/** @see java.lang.Comparable#compareTo(java.lang.Object) */
	@Override
	public int compareTo(MailingAddressee o) {
		// Compares by type.
		int result = 0;
		if (type != null)
			result = type.compareTo(o.type);

		// If it's a tie, compare by the superclass' criteria (namely, by UUID).
		if (result == 0)
			result = super.compareTo(o);

		return result;
	}
}
