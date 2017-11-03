package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

import org.apache.axis2.dataretrieval.OutputForm;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.resources.selectors.Date;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axiastudio.iwas.IWas;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.tn.rivadelgarda.comune.gda.docer.DocerHelper;
import it.tn.rivadelgarda.comune.gda.docer.KeyValuePairFactory;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.SetAclToExternalIdRequest;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.StampData;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.UploadAllegatoResponse;
import it.tn.rivadelgarda.comune.gda.docer.exceptions.DocerHelperException;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento;
import it.tn.rivadelgarda.comune.gda.docer.keys.MetadatiDocumento.TIPO_COMPONENTE_VALUES;

@Api(value = "Docer API")
@Path("/docer")
public class ServiceDocer extends ServiceBase {

	@ApiOperation(value = "/documents", notes = "ritorna documenti (e tutti i metadati) con uno specifico metadato EXTERNAL_ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocuments(
			@ApiParam(value = "attributo EXTERNAL_ID da cercare") @QueryParam("externalId") String externalId,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("externalId={}", externalId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNoneBlank(externalId)) {
				// List<Map<String, String>> documents =
				// docer.searchDocumentsByExternalIdAllAndRelatedAll(externalId);
				List<Map<String, String>> documents = docer.searchDocumentsByExternalIdFirstAndRelated(externalId);
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

	@ApiOperation(value = "/documents", notes = "permette di recuperare la lista dei Documenti contenuti in una Folder del DMS")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = String.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocuments(@ApiParam(value = "documentId della folder") @PathParam("id") String folderId,
			@QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("id={}", folderId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {

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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/profiles", notes = "ritorna l'elenco dei medatadi dei documents di una folder")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{id}/profiles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocumentsProfiles(@PathParam("id") String folderId, @QueryParam("utente") String utente) {
		Response response = null;
		try (DocerHelper docer = getDocerHelper(utente)) {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("id={}", folderId);
			logger.debug("utente={}", utente);

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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/childs", notes = "tutti le folder + tutti i profili")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "List"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{id}/childs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFolderDocumentsChilds(@PathParam("id") String folderId, @QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("id={}", folderId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/profile", notes = "profilo di un documento")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = Map.class, responseContainer = "Map"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{id}/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentProfile(@PathParam("id") String documentId, @QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("id={}", documentId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNoneBlank(documentId)) {
				Map<String, String> documentData = docer.getProfileDocumentMap(documentId);
				String json = new Gson().toJson(documentData);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/versions", notes = "elenco delle versioni")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "success", response = String.class, responseContainer = "Map"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{id}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentVersions(@PathParam("id") String documentId, @QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("documentId={}", documentId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			if (StringUtils.isNoneBlank(documentId)) {
				List<String> documentVersions = docer.getVersions(documentId);
				String json = new Gson().toJson(documentVersions);
				response = Response.ok(json).build();
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/download", notes = "download di una versione", produces = MediaType.APPLICATION_OCTET_STREAM)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{documentId}/download/{versionNumber}")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentDownloadVersion(@PathParam("documentId") String documentId,
			@PathParam("versionNumber") String versionNumber, @QueryParam("stamp") final String stampParam,
			@QueryParam("utente") String utente) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("documentId={}", documentId);
			logger.debug("stamp={}", stampParam);
			logger.debug("utente={}", utente);

			if (StringUtils.isNoneBlank(documentId)) {
				try (DocerHelper docer = getDocerHelper(utente)) {
					Map<String, String> documentMetadata = docer.getProfileDocumentMap(documentId);
					final String fileName = documentMetadata.get(MetadatiDocumento.DOCNAME.getValue());

//					// String versionNumber = "";
//					if (StringUtils.isBlank(versionNumber)) {
//						List<String> versioni = docer.getVersions(documentId);
//						for (String v : versioni) {
//							versionNumber = v;
//							break;
//						}
//					}

					// lettura del file
					final InputStream documentInputStream = docer.getDocumentStream(documentId, versionNumber);

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
//								byte[] data = documentStream.; // Files.readAllBytes(path);
//								output.write(data);
//								output.flush();
								
								if (StringUtils.isNotBlank(stampParam) && fileName.endsWith(".pdf")) {
									// se il file è un PDF applichiamo watermark
									// stamp sarà un JSON di dati da utilizzare
									StampData stampData = new Gson().fromJson(stampParam, StampData.class);
									// applyStamp(stampData, documentStream);
									applyStamp(stampData, documentInputStream, output);
								} else {
									IOUtils.copy(documentInputStream, output);
									// output.write();
									output.flush();
								}
							} catch (Exception e) {
								throw new WebApplicationException("File Not Found !!");
							}
						}
					};
					response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
							.header("content-disposition", "attachment;filename=\"" + fileName + "\"").build();
				}
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/download", notes = "download di un  document", produces = MediaType.APPLICATION_OCTET_STREAM)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/{documentId}/download")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentDownload(@PathParam("documentId") String documentId, @QueryParam("stamp") String stamp,
			@QueryParam("utente") String utente) {
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("documentId={}", documentId);
		logger.debug("stamp", stamp);
		logger.debug("utente={}", utente);
		return getDocumentDownloadVersion(documentId, "", stamp, utente);
	}

	@ApiOperation(value = "/downloadall", notes = "download all documents by external_id in a single zip", produces = MediaType.APPLICATION_OCTET_STREAM)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@GET
	@Path("/documents/downloadall")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response getDocumentDownloadAll(@QueryParam("externalId") String externalId,
			@QueryParam("utente") String utente) {
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("externalId={}", externalId);
		logger.debug("utente={}", utente);
		Response response = null;
		try {
			if (StringUtils.isNoneBlank(externalId)) {
				try (DocerHelper docer = getDocerHelper(utente)) {
					
					String zipName = "all_" + externalId + ".zip";

					// lista documenti da External_ID
					final List<Map<String, String>> documents = docer.searchDocumentsByExternalIdAll(externalId);
					final Map<String, byte[]> documentBytes = new HashMap<>();
					for (Map<String, String> metadata : documents) {
						String documentId = KeyValuePairFactory.getMetadata(metadata, MetadatiDocumento.DOCNUM);
						String fileName = KeyValuePairFactory.getMetadata(metadata, MetadatiDocumento.DOCNAME);
						// calcolo versione documento
						String versionNumber = "";
						List<String> versioni = docer.getVersions(documentId);
						for (String v : versioni) {
							versionNumber = v;
							break;
						}
						// scarico documento
						final byte[] documentStream = docer.getDocument(documentId, versionNumber);
						documentBytes.put(fileName, documentStream);
					}
					final byte[] documentStreamZipped = zipBytes(documentBytes);
					
					StreamingOutput fileStream = new StreamingOutput() {
						@Override
						public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
							try {
								// java.nio.file.Path path =
								// Paths.get(filePath);
								byte[] data = documentStreamZipped; // Files.readAllBytes(path);
								output.write(data);
								output.flush();
							} catch (Exception e) {
								throw new WebApplicationException("File Not Found !!");
							}
						}
					};

					response = Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
							.header("content-disposition", "attachment;filename=\"" + zipName + "\"").build();
				}
			}
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/upload", notes = "upload di un document", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@POST
	@Path("/documents/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadByExternalId(
			@ApiParam(name = "externalId", value = "EXTERNAL_ID da impostare come metadato document", required = false) @FormDataParam("externalId") String externalId,
			@ApiParam(name = "acl", value = "ACL da applicare al documento", required = false) @FormDataParam("acl") String acl,
			@ApiParam(name = "abstract", value = "ABSTRACT da impostare come metadato document", required = false) @FormDataParam("abstract") String abstractDocumento,
			@ApiParam(name = "tipoComponente", value = "TIPO_COMPONENTE da impostare come metadato document", required = true) @FormDataParam("tipoComponente") String tipoComponente,
			@ApiParam(name = "utente", value = "utente accesso DOCER", required = false) @FormDataParam("utente") String utente,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("externalId={}", externalId);
			logger.debug("abstract={}", abstractDocumento);
			logger.debug("tipoComponente={}", tipoComponente);
			logger.debug("acl={}", acl);
			logger.debug("utente={}", utente);

			final String fileName = fileDisposition.getFileName();

			// String filePath =
			// restServletContext.getRealPath("/WEB-INF/test-docer/" +
			// fileName);
			// File f = new File(filePath);
			// FileUtils.copyInputStreamToFile(fileInputStream, f);

			// VERIFICA PARAMETRI
			// gestione del tipo componente passato
			TIPO_COMPONENTE_VALUES tipoComponenteVal = null;
			try {
				tipoComponenteVal = TIPO_COMPONENTE_VALUES.valueOf(tipoComponente);
			} catch (Exception ex) {
				throw new DocerHelperException("Tipo Componente '" + tipoComponente + "' non valido.");
			}
			Map<String, Integer> aclMap = new HashMap<String, Integer>();
			try {
				if (StringUtils.isNotBlank(acl)) {
					Type type = new TypeToken<Map<String, Integer>>() {
					}.getType();
					aclMap = new Gson().fromJson(acl, type);
				}
			} catch (Exception ex) {
				throw new DocerHelperException("ACL specificate '" + acl + "' non valide.");
			}

			try (DocerHelper docer = getDocerHelper(utente)) {
				logger.debug("invio file '{}' a docer", fileName);
				String timestamp = String.valueOf(new Date().getMillis());
				String documentId = docer.createDocumentTypeDocumentoAndRelateToExternalId(fileName,
						IOUtils.toByteArray(fileInputStream), tipoComponenteVal, abstractDocumento, externalId);
				logger.debug("creato in docer con id {}", documentId);
				if (aclMap != null && !aclMap.isEmpty()) {
					docer.setACLDocumentConvert(documentId, aclMap);
					logger.debug("impostato in docer acl {} per {}", aclMap, documentId);
				} else {
					logger.warn("nessuna acl specificata per il documento {}", documentId);
				}
			}
			response = Response.ok(responseData).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/upload", notes = "upload di un document su una folder", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@POST
	@Path("/documents/{folderId}/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadOnFolder(@PathParam("folderId") String folderId,
			@FormDataParam("abstract") String abstractDocumento, @FormDataParam("tipoComponente") String tipoComponente,
			@FormDataParam("utente") String utente, @FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		// public Response upload(@FormDataParam("file") InputStream file,
		// @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("folderId={}", folderId);
			logger.debug("abstract={}", abstractDocumento);
			logger.debug("tipoComponente={}", tipoComponente);
			logger.debug("utente={}", utente);
			// logger.debug("{}", new Gson().toJson(allegatoRequest));

			// AllegatoPec allegato = MessaggioPecBL.saveFile(getContextEmf(),
			// allegatoRequest, file);
			final String fileName = fileDisposition.getFileName();

			// String filePath =
			// restServletContext.getRealPath("/WEB-INF/test-docer/" +
			// fileName);
			// File f = new File(filePath);
			// FileUtils.copyInputStreamToFile(fileInputStream, f);

			try (DocerHelper docer = getDocerHelper(utente)) {
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
				String documentId = docer.createDocumentTypeDocumento(fileName, IOUtils.toByteArray(fileInputStream),
						tipoComponenteVal, abstractDocumento, null);
				logger.debug("creato in docer con id {}", documentId);
				try {
					docer.addToFolderDocument(folderId, documentId);
					logger.debug("aggiunto document {} a folder {}", documentId, folderId);
				} catch (Exception ex) {
					// nel caso si verifichi un errore nel collegamento
					// document-folder elimino il documen appena creato
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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@ApiOperation(value = "/upload", notes = "upload di un versione di document", consumes = MediaType.MULTIPART_FORM_DATA)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success"),
			@ApiResponse(code = 500, message = "error") })
	@POST
	@Path("/documents/{documentId}/versione")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadVersione(@PathParam("documentId") String documentId,
			@FormDataParam("abstract") String abstractDocumento, @FormDataParam("utente") String utente,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("documentId={}", documentId);
		logger.debug("abstract={}", abstractDocumento);
		logger.debug("utente={}", utente);

		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			final String fileName = fileDisposition.getFileName();
			try (DocerHelper docer = getDocerHelper(utente)) {
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
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@DELETE
	@Path("/documents/{id}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public Response documentDelete(@PathParam("id") String documentId, @QueryParam("utente") String utente) {
		Response response = null;
		logger.debug("{}", uriInfo.getAbsolutePath());
		logger.debug("documentId={}", documentId);
		logger.debug("utente={}", utente);

		try (DocerHelper docer = getDocerHelper(utente)) {
			boolean res = docer.deleteDocument(documentId);
			response = Response.ok(res).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	// @ApiOperation(value = "/metadata/externalid", notes = "download di tutti
	// i document con un certo EXTERNAL_ID",
	// produces=MediaType.APPLICATION_OCTET_STREAM)
	// @ApiResponses(value = {
	// @ApiResponse(code = 200, message = "success"),
	// @ApiResponse(code = 500, message = "error")
	// })
	// @GET
	// @Path("/metadata/externalid/{externalId}/downloadall")
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response downloadAll(@PathParam("externalId") String externalId) {
	// Response response = null;
	// try (
	// DocerHelper docer = getDocerHelper()) {
	// logger.debug("{}", uriInfo.getAbsolutePath());
	// logger.debug("externalId={}", externalId);
	//
	// List<String> files = docer.searchDocumentsByExternalIdAll(externalId);
	//
	// response = Response.ok(res).build();
	// } catch (Exception ex) {
	// logger.error("INTERNAL_SERVER_ERROR", ex);
	// response =
	// Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
	// }
	// return response;
	// }

	/**
	 * CODICE PER STAMP WATERMARK
	 * @param stamp
	 * @param inputStream
	 * @return
	 */
	private void applyStamp(StampData stamp, InputStream inputStream, OutputStream outputStream) {
		/*
		 * https://gist.github.com/tizianolattisi/d02f951b4e08216f968d3d1c1f46e30a
		 * https://github.com/axiastudio/iwas
		 */
				
		IWas iwas = IWas.create();
		try {
            iwas.load(inputStream).offset(stamp.offsetx, stamp.offsety);
            int riga = 0;
            for (String testo: stamp.rows) {
            	riga++;
                iwas.text(testo, 9, (float) (riga - 1) * 9, 0f, stamp.rotation);
            }
            iwas.toStream(outputStream);
        } catch (Exception ex) {
        	logger.error("applyStamp", ex);
        }
	}

	@POST
	@Path("/documents/setacl")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "/documents/setacl", notes = "imposta ACL su tutti i documenti corrispondenti ad EXTERNAL_ID")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "elenco dei documentId dove sono state applicate le ACL", response = String.class, responseContainer = "List"),
			@ApiResponse(code = 400, message = "errore mancanza dati per applicare ACL"),
			@ApiResponse(code = 500, message = "error") })
	public Response setAclToExternalId(@ApiParam SetAclToExternalIdRequest request) {
		Response response = null;
		List<String> responseData = new ArrayList<>();
		try {
			if (request != null) {
				String externalId = request.getExternalId();
				String acl = request.getAcl();
				String utente = request.getUtente();

				logger.debug("{}", uriInfo.getAbsolutePath());
				logger.debug("externalId={}", externalId);
				logger.debug("acl={}", acl);
				logger.debug("utente={}", utente);

				Map<String, Integer> aclMap = new HashMap<String, Integer>();
				try {
					if (StringUtils.isNotBlank(acl)) {
						Type type = new TypeToken<Map<String, Integer>>() {
						}.getType();
						aclMap = new Gson().fromJson(acl, type);
					}
				} catch (Exception ex) {
					throw new DocerHelperException("ACL specificate '" + acl + "' non valide.");
				}

				try (DocerHelper docer = getDocerHelper(utente)) {
					if (aclMap != null && !aclMap.isEmpty()) {
						responseData = docer.setACLDocumentsByExternalId(externalId, aclMap);
						logger.debug("impostato in docer acl {} per {} documents da externalId={}", aclMap,
								responseData.size(), externalId);
					} else {
						logger.warn("nessuna acl specificata");
						throw new DocerHelperException("nessuna acl specificata");
					}
				}
				response = Response.ok(responseData).build();
			} else {
				throw new DocerHelperException("");
			}
		} catch (DocerHelperException ex) {
			logger.error("BAD_REQUEST", ex);
			response = Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN)
					.build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage())
					.type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	private byte[] zipBytes(Map<String, byte[]> inputs) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ZipOutputStream zos = new ZipOutputStream(baos)) {
			// for (byte[] input : inputs) {
			for (String filename : inputs.keySet()) {
				byte[] input = inputs.get(filename);
				ZipEntry entry = new ZipEntry(filename);
				entry.setSize(input.length);
				zos.putNextEntry(entry);
				zos.write(input);
				zos.closeEntry();
			}
			// zos.close();
		}
		return baos.toByteArray();
	}	
}
