package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;






import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	public enum FetchType {
		NUR_ARTIKEL,
		MIT_BESTELLUNGEN,
		MIT_WARTUNGSVERTRAEGEN
	}
	
	public enum OrderType {
		KEINE,
		ID
	}
	
	
	@PersistenceContext
	private transient EntityManager em;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	@Inject
	private ValidatorProvider validatorProvider;
	
	
	public Artikel createArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return artikel;
		}

		// Die Methode ist in Agabe 2 vorhanden , muss kopieren , und gemacht !
		//validateArtikel(artikel, locale, Default.class);
		
		// Pruefung, ob die Bezeichnung schon existiert
		try {
			LOGGER.trace("Prufung der Bezeichnung");
			em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
			  .setParameter(Artikel.PARAM_ARTIKEL_BEZEICHNUNG, artikel.getBezeichnung())
			  .getSingleResult();
			throw new BezeichnungExistsException(artikel.getBezeichnung());
		}
		catch (NoResultException e) {
			// Noch kein Artikel mit dieser Bezeichnung
			LOGGER.trace("Bezeichnung existiert noch nicht");
		}
		LOGGER.trace("Bevor Persist");
		em.persist(artikel);
		LOGGER.trace("Nach Persist");
		return artikel;		
	}
	
	/**
	 */
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}

	
	/**
	 */
	public Artikel findArtikelById(Long id) {
		final Artikel artikel = em.find(Artikel.class, id);
		return artikel;
	}
	
	/**
	 */
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		/**
		 * SELECT a
		 * FROM   Artikel a
		 * WHERE  a.id = ? OR a.id = ? OR ...
		 */
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		//final Path<String> idPath = a.get(Artikel_.id);   // Metamodel-Klassen funktionieren nicht mit Eclipse
		
		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			// Mind. 2x id, durch OR verknuepft
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}

	
	/**
	 */
	public Artikel findArtikelByBezeichnung(String bezeichnung, Locale locale) {
		LOGGER.trace("In Artikel Services");
		validateBezeichnung(bezeichnung, locale);
		LOGGER.trace("Nach Validation");
		try {
			final Artikel artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
					                      .setParameter(Artikel.PARAM_ARTIKEL_BEZEICHNUNG, bezeichnung)
					                      .getSingleResult();
			return artikel;
		}
		catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 */
	public Artikel updateArtikel(Artikel artikel, Locale locale) {
		if (artikel == null) {
			return null;
		}

		// Werden alle Constraints beim Modifizieren gewahrt?
		validateArtikel(artikel, locale, Default.class, IdGroup.class);
		
		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id gesucht wird
		em.detach(artikel);
		
		final Artikel tmp = findArtikelById(artikel.getId());
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != artikel.getId().longValue()) {
				//anderes Objekt mit gleicher Bezeichnung
				throw new BezeichnungExistsException(artikel.getBezeichnung());
			}
		}

		em.merge(artikel);
		return artikel;
	}

	private void validateBezeichnung(String bezeichnung, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "bezeichnung",
				                                                                           bezeichnung,
			                                                                         Default.class); 
		if (!violations.isEmpty())
			throw new InvalidBezeichnungException(bezeichnung, violations);
			
			
		}
	
	/**
	 */
	public List<Artikel> findArtikelByPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
	
	private void validateArtikelId(long artikelId, Locale locale) {
		
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Artikel>> violations = validator.validateValue(Artikel.class,
				                                                                           "id",
				                                                                           artikelId,
				                                                                           IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidArtikelIdException(artikelId, violations);
	}
	
	
	private void validateArtikel(Artikel artikel, Locale locale, Class<?>... groups) {
		
		// Werden alle Constraints beim Einfuegen gewahrt?
				final Validator validator = validatorProvider.getValidator(locale);
				
				final Set<ConstraintViolation<Artikel>> violations = validator.validate(artikel, groups);
				if (!violations.isEmpty()) {
					throw new InvalidArtikelException(artikel, violations);
				}
		
	}
	
	
}
