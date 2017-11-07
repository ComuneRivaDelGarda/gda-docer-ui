package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento;

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
	@ApiOperation(value = "/registro/giornaliero", notes = "RG param IN data (GG singolo) EXT=PROTOCOLLO_X,PROTOCOLLO_Y prendi tutti tutti i DOC con quegli EXTERNAL_ID (da - a) creati quel giorno --> EXTERNAL_ID|NOME|HASH")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/registro/giornaliero")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistroGiornaliero(
			@ApiParam(value = "parametro EXTERNAL_ID inzio") @QueryParam("x") String x,
			@ApiParam(value = "parametro EXTERNAL_ID fine") @QueryParam("y") String y,
			@ApiParam(value = "parametro DATA da cercare (formato yyyyMMdd)") @QueryParam("data") String data,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("x={}", x);
		logger.debug("y={}", y);
		logger.debug("data={}", data);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNotBlank(x) && StringUtils.isNotBlank(y) && StringUtils.isNoneBlank(data)) {
				Date param = new SimpleDateFormat("yyyyMMdd").parse(data);
				Set<Map<String, String>> documents = docer.searchDocumentsByExternalIdRangeAndDate(x, y, "protocollo_", param, true);
				documents = MetadatiHelper.mapReduce(documents, MetadatiDocumento.EXTERNAL_ID, MetadatiDocumento.CREATION_DATE, MetadatiDocumento.ABSTRACT, MetadatiDocumento.DOC_HASH);
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
	
	/**
	 * REGISTRO GIORNALIERO MODIFICHE (chiamato a mezzanotte, riferito al giorno appena concluso)
	 * Parametri chiamata: X, DATA   (X primo EXT_ID del giorno DATA
	 * Tutti i documenti inseriti/modificati in data DATA con external id precedente a PROTOCOLLO_X
	 * @param externalId
	 * @param utente
	 * @return
	 */
	@ApiOperation(value = "/registro/giornaliero/modifiche", notes = "RM param IN PROTOCOLLO IN tutti i documenti aggiunti o modificati (OGGI, giorno che si sta concludendo) con EXTERNAL_ID precedente a PROTOCOLLO_X")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/registro/giornaliero/modifiche")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistroModifiche(
			@ApiParam(value = "attributo EXTERNAL_ID da cercare") @QueryParam("x") String externalId,
			@ApiParam(value = "parametro DATA da cercare (formato yyyyMMdd)") @QueryParam("data") String data,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("externalId={}", externalId);
		logger.debug("data={}", data);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNotBlank(externalId) && StringUtils.isNoneBlank(data)) {
				Date param = new SimpleDateFormat("yyyyMMdd").parse(data);
				Set<Map<String, String>> documents = docer.searchDocumentsByExternalIdRangeAndDate(externalId, null, "protocollo_", param, true);
				documents = MetadatiHelper.mapReduce(documents, MetadatiDocumento.EXTERNAL_ID, MetadatiDocumento.CREATION_DATE, MetadatiDocumento.ABSTRACT, MetadatiDocumento.DOC_HASH);
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
