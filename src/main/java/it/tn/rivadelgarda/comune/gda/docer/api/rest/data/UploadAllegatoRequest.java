package it.tn.rivadelgarda.comune.gda.docer.api.rest.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FilenameUtils;

@XmlRootElement
public class UploadAllegatoRequest {

	private String fileName;
	private String titolo;
	private String contentType;
	private long size;

	private long idMessaggio;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(long idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	@XmlTransient
	public static synchronized UploadAllegatoRequest fromFile(String file) throws IOException {
		UploadAllegatoRequest allegato = null;
		File f = new File(file);
		if (f.exists()) {
			allegato = new UploadAllegatoRequest();
			allegato.setFileName(FilenameUtils.getName(file));
			allegato.setSize(f.length());
			allegato.setContentType(Files.probeContentType(f.toPath()));
			// allegato.setFileBytes(FileUtils.readFileToByteArray(f));
			// allegato.setFileBase64(new
			// String(Base64.encodeBase64(FileUtils.readFileToByteArray(f))));
		}
		return allegato;
	}

}
