package br.com.engenhodesoftware.sigme.core.application;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.engenhodesoftware.sigme.core.domain.Spiritist;
import br.com.engenhodesoftware.sigme.core.persistence.SpiritistDAO;
import br.com.engenhodesoftware.util.ejb3.application.CrudException;
import br.com.engenhodesoftware.util.ejb3.application.CrudOperation;
import br.com.engenhodesoftware.util.ejb3.application.CrudService;
import br.com.engenhodesoftware.util.ejb3.persistence.BaseDAO;
import br.com.engenhodesoftware.util.people.persistence.exceptions.MultiplePersistentObjectsFoundException;
import br.com.engenhodesoftware.util.people.persistence.exceptions.PersistentObjectNotFoundException;

/**
 * Stateless session bean implementing the "Manage Spiritist" use case component. See the implemented interface
 * documentation for details.
 * 
 * @author Vitor E. Silva Souza (vitorsouza@gmail.com)
 * @see br.com.engenhodesoftware.sigme.core.application.ManageSpiritistsService
 */
@Stateless
public class ManageSpiritistsServiceBean extends CrudService<Spiritist> implements ManageSpiritistsService {
	/** Serialization id. */
	private static final long serialVersionUID = 1L;

	/** The logger. */
	private static final Logger logger = Logger.getLogger(ManageSpiritistsServiceBean.class.getCanonicalName());

	/** The DAO for Spiritist objects. */
	@EJB
	private SpiritistDAO spiritistDAO;

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#createNewEntity() */
	@Override
	protected Spiritist createNewEntity() {
		return new Spiritist();
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#getDAO() */
	@Override
	protected BaseDAO<Spiritist> getDAO() {
		return spiritistDAO;
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#authorize() */
	@Override
	public void authorize() {
		// Overridden to implement authorization. @RolesAllowed is placed in the whole class.
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#validateCreate(br.com.engenhodesoftware.util.ejb3.persistence.PersistentObject) */
	@Override
	public void validateCreate(Spiritist entity) throws CrudException {
		// Possibly throwing a CRUD Exception to indicate validation error.
		CrudException crudException = null;
		String crudExceptionMessage = "The spiritist \"" + entity.getEmail() + "\" cannot be created due to validation errors.";

		// Validates business rules.
		// Rule 1: cannot have two spiritists with the same email.
		try {
			Spiritist anotherEntity = spiritistDAO.retrieveByEmail(entity.getEmail());
			if (anotherEntity != null) {
				logger.log(Level.INFO, "Creation of spiritist \"{0}\" violates validation rule 1: spiritist with id {1} has same email", new Object[] { entity.getEmail(), anotherEntity.getId() });
				crudException = addValidationError(crudException, crudExceptionMessage, "email", "manageSpiritists.error.repeatedEmail", anotherEntity.getLastUpdateDate());
			}
		}
		catch (PersistentObjectNotFoundException e) {
			logger.log(Level.FINE, "Rule 1 OK - there's no other spiritist with the same email: {0}", entity.getEmail());
		}
		catch (MultiplePersistentObjectsFoundException e) {
			logger.log(Level.WARNING, "Creation of spiritist with email \"" + entity.getEmail() + "\" threw an exception: a query for spiritists with this email returned multiple results!", e);
			crudException = addValidationError(crudException, crudExceptionMessage, "name", "manageSpiritists.error.multipleInstancesError");
		}

		// If one of the rules was violated, throw the exception.
		if (crudException != null)
			throw crudException;
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#validateUpdate(br.com.engenhodesoftware.util.ejb3.persistence.PersistentObject) */
	@Override
	public void validateUpdate(Spiritist entity) throws CrudException {
		// Possibly throwing a CRUD Exception to indicate validation error.
		CrudException crudException = null;
		String crudExceptionMessage = "The spiritist \"" + entity.getEmail() + "\" cannot be updated due to validation errors.";

		// Validates business rules.
		// Rule 1: cannot have two spiritists with the same name.
		try {
			Spiritist anotherEntity = spiritistDAO.retrieveByEmail(entity.getEmail());
			if ((anotherEntity != null) && (!anotherEntity.getId().equals(entity.getId()))) {
				logger.log(Level.INFO, "Update of spiritist \"{0}\" violates validation rule 1: spiritist with id {1} has same email", new Object[] { entity.getEmail(), anotherEntity.getId() });
				crudException = addValidationError(crudException, crudExceptionMessage, "email", "manageSpiritists.error.repeatedEmail", anotherEntity.getLastUpdateDate());
			}
		}
		catch (PersistentObjectNotFoundException e) {
			logger.log(Level.FINE, "Rule 1 OK - there's no other spiritist with the same email: {0}", entity.getEmail());
		}
		catch (MultiplePersistentObjectsFoundException e) {
			logger.log(Level.WARNING, "Creation of spiritist with email \"" + entity.getEmail() + "\" threw an exception: a query for spiritists with this email returned multiple results!", e);
			crudException = addValidationError(crudException, crudExceptionMessage, "name", "manageSpiritists.error.multipleInstancesError");
		}

		// If one of the rules was violated, throw the exception.
		if (crudException != null)
			throw crudException;
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#validate(br.com.engenhodesoftware.util.ejb3.persistence.PersistentObject, br.com.engenhodesoftware.util.ejb3.persistence.PersistentObject) */
	@Override
	protected Spiritist validate(Spiritist newEntity, Spiritist oldEntity) {
		// Never changes the password on updates.
		if (oldEntity != null) newEntity.setPassword(oldEntity.getPassword());
		
		// Sets the current date/time as last update date of the institution.
		newEntity.setLastUpdateDate(new Date(System.currentTimeMillis()));

		return newEntity;
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#log(br.com.engenhodesoftware.util.ejb3.application.CrudOperation, br.com.engenhodesoftware.util.ejb3.persistence.PersistentObject) */
	@Override
	protected void log(CrudOperation operation, Spiritist entity) {
		switch (operation) {
		case CREATE:
		case UPDATE:
			logger.log(Level.FINER, "Storing spiritist: {1} ({0})...", new Object[] { entity.getName(), entity.getEmail() });
			logger.log(Level.FINER, "Persisting data...\n\t- Birthdate = {0}\n\t- Gender = {1}\n\t- Short name = {2}\n\t- Address = {3}\n\t- Telephones = {4}\n\t- {5} attendances", new Object[] { entity.getBirthDate(), entity.getGender(), entity.getShortName(), entity.getAddress(), entity.getTelephones(), entity.getAttendances().size() });
			break;
		case RETRIEVE:
			logger.log(Level.FINE, "Loaded spiritist with id {0} ({1})...", new Object[] { entity.getId(), entity.getEmail() });
			break;
		case DELETE:
			logger.log(Level.FINE, "Deleted spiritist with id {0} ({1})...", new Object[] { entity.getId(), entity.getEmail() });
			break;
		}
	}

	/** @see br.com.engenhodesoftware.util.ejb3.application.CrudService#log(br.com.engenhodesoftware.util.ejb3.application.CrudOperation, java.util.List, int[]) */
	@Override
	protected void log(CrudOperation operation, List<Spiritist> entities, int ... interval) {
		switch (operation) {
		case LIST:
			logger.log(Level.FINE, "Retrieved spiritists in interval [{0}, {1}): {2} spiritist(s) loaded.", new Object[] { interval[0], interval[1], entities.size() });
		}
	}
}
