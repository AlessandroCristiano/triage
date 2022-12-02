package it.prova.triage.dto;

public class DottoreRequestDTO {
	
	private String codiceFiscale;
	private String codiceDottore;

	public DottoreRequestDTO() {
		super();
	}

	public DottoreRequestDTO(String codiceFiscale, String codiceDottore) {
		super();
		this.codiceFiscale = codiceFiscale;
		this.codiceDottore = codiceDottore;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getCodiceDottore() {
		return codiceDottore;
	}

	public void setCodiceDottore(String codiceDottore) {
		this.codiceDottore = codiceDottore;
	}	

}
