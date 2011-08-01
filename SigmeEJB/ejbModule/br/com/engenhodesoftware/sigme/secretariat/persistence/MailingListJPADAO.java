package br.com.engenhodesoftware.sigme.secretariat.persistence;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import br.com.engenhodesoftware.sigme.secretariat.domain.MailingList;
import br.com.engenhodesoftware.util.ejb3.persistence.BaseJPADAO;
import br.com.engenhodesoftware.util.people.persistence.exceptions.MultiplePersistentObjectsFoundException;
import br.com.engenhodesoftware.util.people.persistence.exceptions.PersistentObjectNotFoundException;

/**
 * TODO: document this type.
 * 
 * @author Vitor Souza (vitorsouza@gmail.com)
 */
@Stateless
public class MailingListJPADAO extends BaseJPADAO<MailingList> implements MailingListDAO {
	/** Serialization id. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = Logger.getLogger(MailingListJPADAO.class.getCanonicalName());

	/** The application's persistent context provided by the application server. */
	@PersistenceContext
	private EntityManager entityManager;

	/** @see br.com.engenhodesoftware.util.ejb3.persistence.BaseDAO#getDomainClass() */
	@Override
	public Class<MailingList> getDomainClass() {
		return MailingList.class;
	}

	/** @see br.com.engenhodesoftware.util.ejb3.persistence.BaseJPADAO#getEntityManager() */
	@Override
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * @see br.com.engenhodesoftware.util.ejb3.persistence.BaseJPADAO#getOrderList(javax.persistence.criteria.CriteriaBuilder,
	 *      javax.persistence.criteria.Root)
	 */
	@Override
	protected List<Order> getOrderList(CriteriaBuilder cb, Root<MailingList> root) {
		// Default order is by name. return "order by obj.name asc";
		return super.getOrderList(cb, root);
	}

	/** @see br.com.engenhodesoftware.sigme.secretariat.persistence.MailingListDAO#retrieveByName(java.lang.String) */
	@Override
	public MailingList retrieveByName(String name) throws PersistentObjectNotFoundException, MultiplePersistentObjectsFoundException {
		logger.log(Level.INFO, "Retrieveing the mailing list whose name is \"{0}\"", name);

		// Constructs the query over the Spiritist class.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MailingList> cq = cb.createQuery(MailingList.class);
		Root<MailingList> root = cq.from(MailingList.class);

		// Filters the query with the email.
		cq.where(cb.equal(root.get(MailingListJPAMetamodel.name), name));
		return executeSingleResultQuery(cq, name);
	}

	/** @see br.com.engenhodesoftware.sigme.secretariat.persistence.MailingListDAO#findByName(java.lang.String) */
	@Override
	public List<MailingList> findByName(String param) {
		logger.log(Level.INFO, "Retrieveing all mailing lists whose name contains \"{0}\"", param);

		// Constructs the query over the Institution class.
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<MailingList> cq = cb.createQuery(MailingList.class);
		Root<MailingList> root = cq.from(MailingList.class);

		// Filters the query with the name or the acronym.
		cq.where(cb.like(cb.lower(root.get(MailingListJPAMetamodel.name)), "%" + param.toLowerCase() + "%"));
		cq.orderBy(cb.asc(root.get(MailingListJPAMetamodel.name)));

		// Returns the list of mailing lists.
		return entityManager.createQuery(cq).getResultList();
	}
}
