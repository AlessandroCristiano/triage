package it.prova.triage.web.api.exception;

public class NonRimovibileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NonRimovibileException(String message) {
		super(message);
	}

}
