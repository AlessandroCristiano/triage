package it.prova.triage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage.model.Paziente;
import it.prova.triage.repository.paziente.PazienteRepository;

@Service
@Transactional
public class PazienteServiceImpl implements PazienteService{
	
	@Autowired
	private PazienteRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<Paziente> listAll() {
		return (List<Paziente>) repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Paziente caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Paziente aggiorna(Paziente pazienteInstance) {
		return repository.save(pazienteInstance);
		
	}

	@Override
	public Paziente inserisciNuovo(Paziente pazienteInstance) {
		return repository.save(pazienteInstance);
		
	}

	@Override
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);
	}

}
