package it.tn.rivadelgarda.comune.gda.docer.api.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.DocumentResponse;
import it.tn.rivadelgarda.comune.gda.docer.api.rest.data.UploadAllegatoResponse;

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
	public Response getLista() {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());

			String path = restServletContext.getRealPath("/WEB-INF/test-docer");
			File[] directories = new File(path).listFiles();
			List<DocumentResponse> responseData = DocumentResponse.fromFiles(directories);
			response = Response.ok(responseData).build();

		} catch (

		Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@GET
	@Path("/documents/{documentId}/versions")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVersions(@PathParam("documentId") int documentId) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);

			List<DocumentResponse> responseData = new ArrayList<>();
			response = Response.ok(responseData).build();

		} catch (

		Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@GET
	@Path("/documents/{documentId}/download")
	// @Produces(MediaType.APPLICATION_JSON)
	public Response getDownload(@PathParam("documentId") int documentId) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", documentId);

			File fileToDownload = null;
			String path = restServletContext.getRealPath("/WEB-INF/test-docer");
			File[] directories = new File(path).listFiles();
			for (File f : directories) {
				if (f.hashCode() == documentId) {
					fileToDownload = f;
					break;
				}
			}

			// final String filePath =
			// restServletContext.getRealPath("/WEB-INF/test-docer/test.pdf");
			final String fileName = fileToDownload.getName();
			final String filePath = fileToDownload.getPath();
			StreamingOutput fileStream = new StreamingOutput() {
				@Override
				public void write(java.io.OutputStream output) throws IOException, WebApplicationException {
					try {
						java.nio.file.Path path = Paths.get(filePath);
						byte[] data = Files.readAllBytes(path);
						output.write(data);
						output.flush();
					} catch (Exception e) {
						throw new WebApplicationException("File Not Found !!");
					}
				}
			};
			return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM).header("content-disposition", "attachment;filename=\"" + fileName + "\"").build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@POST
	@Path("/documents/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(@FormDataParam("titolo") String titolo, @FormDataParam("file") InputStream file, @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		// public Response upload(@FormDataParam("file") InputStream file,
		// @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			// logger.debug("{}", new Gson().toJson(allegatoRequest));

			// AllegatoPec allegato = MessaggioPecBL.saveFile(getContextEmf(),
			// allegatoRequest, file);

			String filePath = restServletContext.getRealPath("/WEB-INF/test-docer/" + fileDisposition.getFileName());
			File f = new File(filePath);
			FileUtils.copyInputStreamToFile(file, f);

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
}
