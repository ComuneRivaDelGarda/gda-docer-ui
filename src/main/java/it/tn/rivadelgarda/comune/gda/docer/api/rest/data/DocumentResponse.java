package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FilenameUtils;

@XmlRootElement
public class DocumentResponse {

	private int id;
	private String nome;
	private String contentType;
	private long dimensioni;
	private boolean directory;
	private Date dataCreazione;
	private Date dataModifica;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getDimensioni() {
		return dimensioni;
	}

	public void setDimensioni(long dimensioni) {
		this.dimensioni = dimensioni;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	@XmlTransient
	public static synchronized List<DocumentResponse> fromFiles(File[] file) throws IOException {
		List<DocumentResponse> res = new ArrayList<>();
		if (file != null)
			for (File f : file) {
				res.add(fromFile(f));
			}
		return res;
	}

	@XmlTransient
	public static synchronized DocumentResponse fromFile(String file) throws IOException {
		File f = new File(file);
		return fromFile(f);
	}

	@XmlTransient
	public static synchronized DocumentResponse fromFile(File f) throws IOException {
		DocumentResponse documentData = null;
		// File f = new File(file);
		if (f != null && f.exists()) {
			documentData = new DocumentResponse();

			documentData.setId(f.hashCode());
			documentData.setNome(FilenameUtils.getName(f.getName()));
			documentData.setDimensioni(f.length());
			documentData.setContentType(Files.probeContentType(f.toPath()));
			documentData.setDirectory(f.isDirectory());

			BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
			documentData.setDataCreazione(new Date(attr.creationTime().toMillis()));
			documentData.setDataCreazione(new Date(attr.lastModifiedTime().toMillis()));
			// allegato.setFileBytes(FileUtils.readFileToByteArray(f));
			// allegato.setFileBase64(new
			// String(Base64.encodeBase64(FileUtils.readFileToByteArray(f))));
		}
		return documentData;
	}
}
