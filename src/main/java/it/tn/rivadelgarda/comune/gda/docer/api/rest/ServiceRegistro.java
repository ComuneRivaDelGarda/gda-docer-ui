package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.tn.rivadelgarda.comune.gda.docer.DocerHelper;
import it.tn.rivadelgarda.comune.gda.docer.MetadatiHelper;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.xml.RegistroGiornaliero;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatoDocer;

@Api(value = "Docer API")
@Path("/docer")
public class ServiceRegistro extends ServiceBase {

	/**
	 * REGISTRO GIORNALIERO (chiamato a mezzanotte, riferito al giorno appena concluso)
	 * Contiene tutti i documenti protocollati nel giorno appena trascorso.
	 * Parametri chiamata: X, Y, DATA    (da EXT_ID X a EXT_ID Y, sono quelli registrati nel giorno DATA, il giorno appena trascorso)
	 * Prende tutti i documenti con gli EXT_ID compresi tra X e Y, creati in data DATA
	 * Caveat: se si richiedesse il registro dell’altro ieri, potrebbe comparire una versione di documento modificata ieri
	 * @param externalId
	 * @param utente
	 * @return
	 */
	@ApiOperation(value = "/registro/giornaliero", notes = "REGISTRO GIORNALIERO param IN data (GG singolo) EXT=PROTOCOLLO_X,PROTOCOLLO_Y prendi tutti tutti i DOC con quegli EXTERNAL_ID (da - a) creati quel giorno --> EXTERNAL_ID|CREATION_DATE|NOME|ABSTRACT|HASH")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/registro/giornaliero")
//	@Produces(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getRegistroGiornaliero(
			@ApiParam(value = "parametro EXTERNAL_ID inzio") @QueryParam("x") String x,
			@ApiParam(value = "parametro EXTERNAL_ID fine") @QueryParam("y") String y,
			@ApiParam(value = "parametro DATA da cercare (formato yyyyMMdd)") @QueryParam("data") String paramData,
			@ApiParam(value = "parametro formato dati da generare (json | xml)") @QueryParam("o") String paramOut,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("x={}", x);
		logger.debug("y={}", y);
		logger.debug("data={}", paramData);
		logger.debug("utente={}", utente);
		logger.debug("o={}", paramOut);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y) && StringUtils.isNoneBlank(paramData)) {
				Date data = new SimpleDateFormat("yyyyMMdd").parse(paramData);
				Collection<Map<String, String>> documents = docer.searchDocumentsByExternalIdRangeAndDate(x, y, "protocollo_", data, true);
				documents = MetadatiHelper.mapReduce(documents, MetadatiDocumento.EXTERNAL_ID, MetadatiDocumento.CREATION_DATE, MetadatiDocumento.DOCNAME, MetadatiDocumento.ABSTRACT, MetadatiDocumento.DOC_HASH);
				
				if (paramOut == null || "json".equalsIgnoreCase(paramOut)) {
					String json = new Gson().toJson(documents);
					response = Response.ok(json, MediaType.APPLICATION_JSON).build();
				} else {
					final String filename = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
					RegistroGiornaliero xml = new RegistroGiornaliero();
					xml.setCollection(documents);
					response = Response.ok(xml, MediaType.APPLICATION_XML)
							.header("Content-Disposition", "inline; filename=\"RG" + filename + ".xml\"").build();
				}
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	/**
	 * REGISTRO GIORNALIERO MODIFICHE (chiamato a mezzanotte, riferito al giorno appena concluso)
	 * Parametri chiamata: X, DATA   (X primo EXT_ID del giorno DATA
	 * Tutti i documenti inseriti/modificati in data DATA con external id precedente a PROTOCOLLO_X
	 * @param externalId
	 * @param utente
	 * @return
	 */
	@ApiOperation(value = "/registro/giornaliero/modifiche", notes = "REGISTRO GIORNALIERO MODIFICHE param IN PROTOCOLLO IN tutti i documenti aggiunti o modificati (OGGI, giorno che si sta concludendo) con EXTERNAL_ID precedente a PROTOCOLLO_X")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/registro/giornaliero/modifiche")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistroModifiche(
			@ApiParam(value = "attributo EXTERNAL_ID da cercare") @QueryParam("x") String externalId,
			@ApiParam(value = "parametro DATA da cercare (formato yyyyMMdd)", required=true) @QueryParam("data") String paramData,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("externalId={}", externalId);
		logger.debug("data={}", paramData);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNoneBlank(paramData)) {
				Date data = new SimpleDateFormat("yyyyMMdd").parse(paramData);
				Collection<Map<String, String>> documents = docer.searchDocumentsByDateAndExternalIdLimit(externalId, "protocollo_", data, true);
				List<MetadatoDocer> templateMetadati = new ArrayList<MetadatoDocer>(Arrays.asList(MetadatiDocumento.EXTERNAL_ID, MetadatiDocumento.CREATION_DATE, MetadatiDocumento.ABSTRACT, MetadatiDocumento.DOC_HASH));
				// @ApiParam(value = "metadati che si vogliono in output") @QueryParam("data") String[] paramMetadati,
//				if (paramMetadati != null) {
//					templateMetadati = new ArrayList<>();
//					for (String metadatoKey : paramMetadati) {
//						MetadatoDocer metadato = MetadatiDocumento.valueOf(metadatoKey);
//						templateMetadati.add(metadato);
//					}
//				}
				documents = MetadatiHelper.mapReduce(documents, templateMetadati);
				String json = new Gson().toJson(documents);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}	
}
