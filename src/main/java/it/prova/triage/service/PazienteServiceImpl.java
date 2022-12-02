package it.prova.triage.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage.model.Paziente;
import it.prova.triage.model.StatoPaziente;
import it.prova.triage.repository.paziente.PazienteRepository;
import it.prova.triage.web.api.exception.NonRimovibileException;
import it.prova.triage.web.api.exception.NotFoundException;

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
		pazienteInstance.setDataRegistrazione(LocalDate.now());
		pazienteInstance.setStato(StatoPaziente.IN_ATTESA_VISITA);
		return repository.save(pazienteInstance);
		
	}

	@Override
	public void rimuovi(Long idToRemove) {
		Paziente pazienteDaControllare = repository.findById(idToRemove).orElse(null);
		
		if(pazienteDaControllare==null) {
			throw new NotFoundException("Nessun paziente con questo id");
		}
		
		if(!pazienteDaControllare.getStato().equals(StatoPaziente.DIMESSO)) {
			throw new NonRimovibileException("Impossibile eliminare un paziente non dimesso");
		}
		repository.deleteById(idToRemove);
	}

	@Override
	public void assegnaDottore(Long id, String codiceDottore) {
		Paziente pazienteDaAssegnareDottore = repository.findById(id).orElse(null);
		
		if(pazienteDaAssegnareDottore==null) {
			throw new NotFoundException("Nessun paziente con questo id non posso aggiungere un dottore");
		}
		
		pazienteDaAssegnareDottore.setCodiceDottore(codiceDottore);
		pazienteDaAssegnareDottore.setStato(StatoPaziente.IN_VISITA);
		repository.save(pazienteDaAssegnareDottore);
		
		System.out.println(repository.findById(id).orElse(null).getCodiceDottore());
	}

	@Override
	public void ricovera(Paziente paziente) {
		paziente.setCodiceDottore(null);
		paziente.setStato(StatoPaziente.RICOVERATO);
		repository.save(paziente);
	}

	@Override
	public void dimetti(Paziente paziente) {
		paziente.setCodiceDottore(null);
		paziente.setStato(StatoPaziente.DIMESSO);
		repository.save(paziente);
		
	}

}
