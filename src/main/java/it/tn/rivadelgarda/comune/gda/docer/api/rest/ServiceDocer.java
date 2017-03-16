package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import com.google.gson.Gson;

import it.tn.rivadelgarda.comune.gda.docer.DocerHelper;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.UploadAllegatoResponse;
import it.tn.rivadelgarda.comune.gda.docer.exceptions.DocerHelperException;
import it.tn.rivadelgarda.comune.gda.docer.keys.DocumentoMetadatiGenericiEnum;
import it.tn.rivadelgarda.comune.gda.docer.keys.DocumentoMetadatiGenericiEnum.TIPO_COMPONENTE;

@Path("/docer")
public class ServiceDocer {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceDocer.class);

	@Context
	protected ServletContext restServletContext;

	@Context
	protected UriInfo uriInfo;

	@GET
	@Path("/documents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocuments(@QueryParam("externalId") String externalId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", externalId);

			if (StringUtils.isNoneBlank(externalId)) {
				List<Map<String, String>> documents = docer.searchDocumentsByExternalId(externalId);
				String json = new Gson().toJson(documents);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	@GET
	@Path("/documents/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocuments(@PathParam("id") String documentId) {
		Response response = null;
		try (
			DocerHelper docer = getDocerHelper()) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);

			if (StringUtils.isNoneBlank(documentId)) {
				List<String> documents = docer.getFolderDocuments(documentId);
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
					final String fileName = documentMetadata.get(DocumentoMetadatiGenericiEnum.DOCNAME.getValue());

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
	
	@GET
	@Path("/documents/{documentId}/download")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response download(@PathParam("documentId") String documentId) {
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("{}", documentId);
		return downloadVersion(documentId, "");
	}
	
	@POST
	@Path("/documents/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadDocer(@FormDataParam("externalId") String externalId, @FormDataParam("abstract") String abstractDocumento, @FormDataParam("tipoComponente") String tipoComponente, @FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("externalId={}", externalId);
			logger.debug("abstract={}", abstractDocumento);
			logger.debug("tipoComponente={}", tipoComponente);

			final String fileName = fileDisposition.getFileName();

//			String filePath = restServletContext.getRealPath("/WEB-INF/test-docer/" + fileName);
//			File f = new File(filePath);
//			FileUtils.copyInputStreamToFile(fileInputStream, f);

			try (
				DocerHelper docer = getDocerHelper()) {
				logger.debug("invio file '{}' a docer", fileName);
				// gestione del tipo componente passato
				TIPO_COMPONENTE tipoComponenteVal = null;
				try {
					tipoComponenteVal = TIPO_COMPONENTE.valueOf(tipoComponente);
				} catch (Exception ex) {
					throw new DocerHelperException("Tipo Componente '" + tipoComponente + "' non valido.");
				}
				String timestamp = String.valueOf(new Date().getMillis());
				String documentId = docer.createDocumentTypeDocumento(fileName, IOUtils.toByteArray(fileInputStream), tipoComponenteVal, abstractDocumento, externalId);
				logger.debug("creato in docer con id {}", documentId);
			}
			response = Response.ok(responseData).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	
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
				TIPO_COMPONENTE tipoComponenteVal = null;
				try {
					tipoComponenteVal = TIPO_COMPONENTE.valueOf(tipoComponente);
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
