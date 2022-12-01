package it.prova.triage.dto;

import javax.validation.constraints.NotBlank;

public class DottoreRequestDTO {
	
	
	@NotBlank(message = "{nome.notblank}")
	private String nome;
	@NotBlank(message = "{cognome.notblank}")
	private String cognome;
	@NotBlank(message = "{codiceDottore.notblank}")
	private String codiceDottore;
	
	public DottoreRequestDTO(String nome, String cognome, String codiceDottore) {
		super();
		this.nome = nome;
		this.cognome = cognome;
		this.codiceDottore = codiceDottore;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceDottore() {
		return codiceDottore;
	}

	public void setCodiceDottore(String codiceDottore) {
		this.codiceDottore = codiceDottore;
	}	

}
