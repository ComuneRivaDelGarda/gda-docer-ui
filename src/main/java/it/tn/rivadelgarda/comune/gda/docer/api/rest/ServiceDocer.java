package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.tn.rivadelgarda.comune.gda.docer.DocerHelper;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.UploadAllegatoResponse;
import it.tn.rivadelgarda.comune.gda.docer.exceptions.DocerHelperException;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento.TIPO_COMPONENTE_VALUES;
import it.tn.rivadelgarda.comune.gda.docer.values.ACL_VALUES;

@Api(value = "Docer API")
@Path("/docer")
public class ServiceDocer {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceDocer.class);

	@Context
	protected ServletContext restServletContext;

	@Context
	protected UriInfo uriInfo;

	@ApiOperation(value = "/documents", notes = "ritorna documenti (e tutti i metadati) con uno specifico metadato EXTERNAL_ID")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
		@ApiResponse(code = 500, message = "error") 
	})		
	@GET
	@Path("/documents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocuments(@ApiParam(value = "attributo EXTERNAL_ID da cercare") @QueryParam("externalId") String externalId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", externalId);

			if (StringUtils.isNoneBlank(externalId)) {
				// List<Map<String, String>> documents = docer.searchDocumentsByExternalIdAllAndRelatedAll(externalId);
				List<Map<String, String>> documents = docer.searchDocumentsByExternalIdFirstAndRelated(externalId);
				String json = new Gson().toJson(documents);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	@ApiOperation(value = "/documents", notes = "permette di recuperare la lista dei Documenti contenuti in una Folder del DMS")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = String.class, responseContainer = "List"),
		@ApiResponse(code = 500, message = "error") 
	})	
	@GET
	@Path("/documents/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocuments(@ApiParam(value = "documentId della folder") @PathParam("id") String folderId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", folderId);

			if (StringUtils.isNoneBlank(folderId)) {
				List<String> documents = docer.getFolderDocuments(folderId);
				String json = new Gson().toJson(documents);
				response = Response.ok(json).build();
			}
			// else {
			// // TEST CARTELLA PREDEFINITA
			// String path =
			// restServletContext.getRealPath("/WEB-INF/test-docer");
			// File[] directories = new File(path).listFiles();
			// List<DocumentResponse> responseData =
			// DocumentResponse.fromFiles(directories);
			// response = Response.ok(responseData).build();
			// }
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/profiles", notes = "ritorna l'elenco dei medatadi dei documents di una folder")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{id}/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocumentsProfiles(@PathParam("id") String folderId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", folderId);

			if (StringUtils.isNoneBlank(folderId)) {
				List<Map<String, String>> data = new ArrayList<>();
				List<Map<String, String>> childDocuments = docer.getProfileDocumentMapByParentFolder(folderId);
				data.addAll(childDocuments);
				String json = new Gson().toJson(data);
				response = Response.ok(json).build();
			}
			// else {
			// // TEST CARTELLA PREDEFINITA
			// String path =
			// restServletContext.getRealPath("/WEB-INF/test-docer");
			// File[] directories = new File(path).listFiles();
			// List<DocumentResponse> responseData =
			// DocumentResponse.fromFiles(directories);
			// response = Response.ok(responseData).build();
			// }
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/childs", notes = "tutti le folder + tutti i profili")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{id}/childs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocumentsChilds(@PathParam("id") String folderId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", folderId);

			if (StringUtils.isNoneBlank(folderId)) {
				List<Map<String, String>> data = new ArrayList<>();

				List<Map<String, String>> childFolders = docer.searchFolders(null, folderId);
				data.addAll(childFolders);

				List<Map<String, String>> childDocuments = docer.getProfileDocumentMapByParentFolder(folderId);
				data.addAll(childDocuments);

				String json = new Gson().toJson(data);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/profile", notes = "profilo di un documento")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "Map"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{id}/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfileDocument(@PathParam("id") String documentId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);
			if (StringUtils.isNoneBlank(documentId)) {
				Map<String, String> documentData = docer.getProfileDocumentMap(documentId);
				String json = new Gson().toJson(documentData);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/versions", notes = "elenco delle versioni")
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success", response = String.class, responseContainer = "Map"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{id}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersions(@PathParam("id") String documentId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);
			if (StringUtils.isNoneBlank(documentId)) {
				List<String> documentVersions = docer.getVersions(documentId);
				String json = new Gson().toJson(documentVersions);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/download", notes = "download di una versione", produces=MediaType.APPLICATION_OCTET_STREAM)
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{documentId}/download/{versionNumber}")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response downloadVersion(@PathParam("documentId") String documentId, @PathParam("versionNumber") String versionNumber) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);
			
			if (StringUtils.isNoneBlank(documentId)) {
				try (
					DocerHelper docer = getDocerHelper()) {
					Map<String, String> documentMetadata = docer.getProfileDocumentMap(documentId);
					final String fileName = documentMetadata.get(MetadatiDocumento.DOCNAME.getValue());

					// String versionNumber = "";
					if (StringUtils.isBlank(versionNumber)) {
						List<String> versioni = docer.getVersions(documentId);
						for (String v : versioni) {
							versionNumber = v;
							break;
						}
					}

					// lettura del file
					final byte[] documentStream = docer.getDocument(documentId, versionNumber);

					// final String filePath =
					// restServletContext.getRealPath("/WEB-INF/test-docer/test.pdf");
					// final String fileName = fileToDownload.getName();
					// final String filePath = fileToDownload.getPath();
					StreamingOutput fileStream = new StreamingOutput() {
						@Override
						public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
							try {
								// java.nio.file.Path path =
								// Paths.get(filePath);
								byte[] data = documentStream; // Files.readAllBytes(path);
								output.write(data);
								output.flush();
							} catch (Exception e) {
								throw new WebApplicationException("File Not Found !!");
							}
						}
					};
					response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM).header("content-disposition", "attachment;filename=\"" + fileName + "\"").build();
				}
			}			
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/download", notes = "download di un  document", produces=MediaType.APPLICATION_OCTET_STREAM)
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success"),
		@ApiResponse(code = 500, message = "error") 
	})
	@GET
	@Path("/documents/{documentId}/download")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response download(@PathParam("documentId") String documentId) {
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("{}", documentId);
		return downloadVersion(documentId, "");
	}

	@ApiOperation(value = "/upload", notes = "upload di un document", consumes=MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success"),
		@ApiResponse(code = 500, message = "error") 
	})
	@POST
	@Path("/documents/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadByExternalId(
			@ApiParam(name="externalId", value = "EXTERNAL_ID da impostare come metadato document", required = false) @FormDataParam("externalId") String externalId,
			@ApiParam(name="acls", value = "ACLs da applicare al documento", required = false) @FormDataParam("acls") String acls,
			@ApiParam(name="abstract", value = "ABSTRACT da impostare come metadato document", required = false) @FormDataParam("abstract") String abstractDocumento,
			@ApiParam(name="tipoComponente", value = "TIPO_COMPONENTE da impostare come metadato document", required = true) @FormDataParam("tipoComponente") String tipoComponente,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("externalId={}", externalId);
			logger.debug("abstract={}", abstractDocumento);
			logger.debug("tipoComponente={}", tipoComponente);
			logger.debug("acls={}", acls);
			
			final String fileName = fileDisposition.getFileName();

//			String filePath = restServletContext.getRealPath("/WEB-INF/test-docer/" + fileName);
//			File f = new File(filePath);
//			FileUtils.copyInputStreamToFile(fileInputStream, f);

			// VERIFICA PARAMETRI
			// gestione del tipo componente passato
			TIPO_COMPONENTE_VALUES tipoComponenteVal = null;
			try {
				tipoComponenteVal = TIPO_COMPONENTE_VALUES.valueOf(tipoComponente);
			} catch (Exception ex) {
				throw new DocerHelperException("Tipo Componente '" + tipoComponente + "' non valido.");
			}
			Map<String, Integer> aclsMap = new HashMap<String, Integer>();
			try {
				if (StringUtils.isNotBlank(acls)) {
					Type type = new TypeToken<Map<String, Integer>>() {
					}.getType();
					aclsMap = new Gson().fromJson(acls, type);
				}
			} catch (Exception ex) {
				throw new DocerHelperException("ACLs specificate '" + acls + "' non valide.");
			}			
			
			try (DocerHelper docer = getDocerHelper()) {
				logger.debug("invio file '{}' a docer", fileName);
				String timestamp = String.valueOf(new Date().getMillis());
				String documentId = docer.createDocumentTypeDocumentoAndRelateToExternalId(fileName, IOUtils.toByteArray(fileInputStream), tipoComponenteVal, abstractDocumento, externalId);
				logger.debug("creato in docer con id {}", documentId);
				if (!aclsMap.isEmpty()) {
					docer.setACLDocumentConvert(documentId, aclsMap);
					logger.debug("impostato in docer acls {} per {}", aclsMap, documentId);
				} else {
					logger.warn("nessuna acl specificata per il documento {}", documentId);
				}
			}
			response = Response.ok(responseData).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/upload", notes = "upload di un document su una folder", consumes=MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success"),
		@ApiResponse(code = 500, message = "error") 
	})	
	@POST
	@Path("/documents/{folderId}/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadOnFolder(@PathParam("folderId") String folderId, @FormDataParam("abstract") String abstractDocumento, @FormDataParam("tipoComponente") String tipoComponente, @FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		// public Response upload(@FormDataParam("file") InputStream file,
		// @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("folderId={}", folderId);
			logger.debug("abstract={}", abstractDocumento);
			logger.debug("tipoComponente={}", tipoComponente);
			// logger.debug("{}", new Gson().toJson(allegatoRequest));

			// AllegatoPec allegato = MessaggioPecBL.saveFile(getContextEmf(),
			// allegatoRequest, file);
			final String fileName = fileDisposition.getFileName();

//			String filePath = restServletContext.getRealPath("/WEB-INF/test-docer/" + fileName);
//			File f = new File(filePath);
//			FileUtils.copyInputStreamToFile(fileInputStream, f);

			try (
				DocerHelper docer = getDocerHelper()) {
				logger.debug("invio file '{}' a docer", fileName);
				
				// gestione del tipo componente passato
				TIPO_COMPONENTE_VALUES tipoComponenteVal = null;
				try {
					tipoComponenteVal = TIPO_COMPONENTE_VALUES.valueOf(tipoComponente);
				} catch (Exception ex) {
					throw new DocerHelperException("Tipo Componente '" + tipoComponente + "' non valido.");
				}
				
				// String documentId = docer.createDocument(fileName, f,
				// TIPO_COMPONENTE.PRINCIPALE, titolo);
				String timestamp = String.valueOf(new Date().getMillis());
				String documentId = docer.createDocumentTypeDocumento(fileName, IOUtils.toByteArray(fileInputStream), tipoComponenteVal, abstractDocumento, null);
				logger.debug("creato in docer con id {}", documentId);
				try {
					docer.addToFolderDocument(folderId, documentId);
					logger.debug("aggiunto document {} a folder {}", documentId, folderId);
				} catch (Exception ex) {
					// nel caso si verifichi un errore nel collegamento document-folder elimino il documen appena creato
					docer.deleteDocument(documentId);
					// restituisco errore originale
					throw ex;
				}
			}

			// responseData.setId(allegato.getId());
			response = Response.ok(responseData).build();

			// } catch (PecException ex) {
			// logger.error("PRECONDITION_FAILED", ex);
			// response =
			// Response.status(Response.Status.PRECONDITION_FAILED).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/upload", notes = "upload di un versione di document", consumes=MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "success"),
		@ApiResponse(code = 500, message = "error") 
	})		
	@POST
	@Path("/documents/{documentId}/versione")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadVersione(@PathParam("documentId") String documentId, @FormDataParam("abstract") String abstractDocumento, @FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("documentId={}", documentId);
			logger.debug("abstract={}", abstractDocumento);
			final String fileName = fileDisposition.getFileName();
			try (
				DocerHelper docer = getDocerHelper()) {
				logger.debug("invio versione '{}' a docer {}", fileName, documentId);
				// String documentId = docer.createDocument(fileName, f,
				// TIPO_COMPONENTE.PRINCIPALE, titolo);
				String timestamp = String.valueOf(new Date().getMillis());
				String versioneId = docer.createVersion(documentId, IOUtils.toByteArray(fileInputStream));
				logger.debug("creato versione con id {}", versioneId);
			}
			response = Response.ok(responseData).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	DocerHelper docer = null;
	String token = null;

	private DocerHelper getDocerHelper() throws Exception {
		if (docer == null) {
			Properties p = new Properties();
			// String path =
			// restServletContext.getRealPath("WEB-INF/config.properties");
			// p.load(getClass().getResourceAsStream("config.properties"));
			// p.load(fileProperties);
			InputStream fileProperties = restServletContext.getResourceAsStream("/WEB-INF/config.properties");
			p.load(fileProperties);
			fileProperties.close();
			logger.debug("caricato configurazione docer da /WEB-INF/config.properties");

			String docerSerivcesUrl = p.getProperty("url");
			String docerUsername = p.getProperty("username");
			String docerPassword = p.getProperty("password");
			docer = new DocerHelper(docerSerivcesUrl, docerUsername, docerPassword);

			token = docer.login();
			logger.debug("connesso a docer con tocken {}", token);
		}
		return docer;
	}
	
	@DELETE
	@Path("/documents/{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDocument(@PathParam("id") String documentId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);
			boolean res = docer.deleteDocument(documentId);
			response = Response.ok(res).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
}
