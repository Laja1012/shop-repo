package de.shop.artikelverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static de.shop.util.Constants.KEINE_ID;







import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.LocaleHelper;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;


@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Consumes
@RequestScoped
@Transactional
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Context
	private UriInfo uriInfo;
	
    @Context
    private HttpHeaders headers;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private LocaleHelper localeHelper;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikelbyId(@PathParam("id") Long id) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		return artikel;
	}
	
	@GET
	@Path ("/bezeichnung/{bezeichnung}")
	public Artikel findArtikelByBezeichnung(@PathParam("bezeichnung") String bezeichnung) {
		
		
		final Locale locale = localeHelper.getLocale(headers);
		final Artikel artikel = as.findArtikelByBezeichnung(bezeichnung, locale);
		//final AbstractKunde kunde = ks.findKundeById(id, , locale);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der Bezeichnung " + bezeichnung;
			//LOGGER.trace("Error In Artikel Ressource");
			throw new NotFoundException(msg);
		}
		return artikel;
	}


	/**
	 * Mit der URL /artikel einen Artikel per PUT aktualisieren
	 * @param artikel zu aktualisierende Daten des Artikel
	 */
	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateArtikel(Artikel artikel) {
		// Vorhandenen Artikel ermitteln
		final Locale locale = localeHelper.getLocale(headers);
		final Artikel origArt = as.findArtikelById(artikel.getId());
		if (origArt == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.tracef("Artikel vorher: %s", origArt);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origArt.setValues(artikel);
		LOGGER.tracef("Artikel nachher: %s", origArt);
		
		// Update durchfuehren
		artikel = as.updateArtikel(origArt, locale);
		if (artikel == null) {
			// TODO msg passend zu locale
			final String msg = "Kein Kunde gefunden mit der ID " + origArt.getId();
			throw new NotFoundException(msg);
		}
	}
	
	
	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createArtikel(Artikel artikel) {
		LOGGER.trace("In Artikel Post");
		LOGGER.tracef("Prob Artikel: %s", artikel);
		
		final Locale locale = localeHelper.getLocale(headers);

		artikel.setId(KEINE_ID);
		//artikel.setBezeichnung(artikel.getBezeichnung());
		
		//kunde = (Privatkunde) ks.createKunde(kunde, locale);
		artikel = as.createArtikel(artikel, locale);
		LOGGER.tracef("Artikel: %s", artikel);
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}
}
