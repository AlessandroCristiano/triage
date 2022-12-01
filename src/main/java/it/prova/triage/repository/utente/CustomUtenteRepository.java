package it.prova.triage.repository.utente;

import java.util.List;

import it.prova.triage.model.Utente;

public interface CustomUtenteRepository {
	List<Utente> findByExample(Utente example);

}
