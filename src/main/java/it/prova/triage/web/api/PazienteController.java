package it.prova.triage.web.api;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage.dto.DottoreRequestDTO;
import it.prova.triage.dto.PazienteDTO;
import it.prova.triage.model.Paziente;
import it.prova.triage.service.PazienteService;
import it.prova.triage.web.api.exception.IdNotNullForInsertException;
import it.prova.triage.web.api.exception.NotFoundException;
import it.prova.triage.web.api.exception.PazienteSenzaDottoreException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/paziente")
public class PazienteController {

	private static final Logger LOGGER = LogManager.getLogger(PazienteController.class);

	@Autowired
	private WebClient webClient;

	@Autowired
	private PazienteService service;

	@GetMapping
	public List<PazienteDTO> listAll() {
		return PazienteDTO.createPazienteDTOListFromModelList(service.listAll());
	}

	@GetMapping("/{id}")
	public PazienteDTO findById(@PathVariable(required = true) Long id) {
		Paziente pazienteDaCaricare = service.caricaSingoloElemento(id);

		if (pazienteDaCaricare == null)
			throw new NotFoundException("Paziente non trovato con id: " + id);

		return PazienteDTO.buildPazienteDTOFromModel(pazienteDaCaricare);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PazienteDTO inserisci(@RequestBody PazienteDTO paziente) {
		if (paziente.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");

		return PazienteDTO.buildPazienteDTOFromModel(service.inserisciNuovo(paziente.buildPazienteModel()));
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public PazienteDTO update(@Valid @RequestBody PazienteDTO paziente, @PathVariable(required = true) Long id) {
		Paziente Paziente = service.caricaSingoloElemento(id);

		if (Paziente == null)
			throw new NotFoundException("Paziente not found con id: " + id);

		paziente.setId(id);
		Paziente pazienteAggiornato = service.aggiorna(paziente.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteAggiornato);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(required = true) Long id) {
		Paziente paziente = service.caricaSingoloElemento(id);

		if (paziente == null)
			throw new NotFoundException("Paziente non trovato con id: " + id);

		service.rimuovi(id);
	}

	@GetMapping("/assegnaPaziente/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void assegnaPaziente(@RequestBody DottoreRequestDTO dottoreRequest, @PathVariable(required = true) Long id) {
		Paziente pazienteModel = service.caricaSingoloElemento(id);
		if (pazienteModel == null) {
			throw new NotFoundException("Paziente inesistente");
		}

		LOGGER.info("....invocazione servizio esterno....");
		DottoreRequestDTO result = webClient.get().uri("/verifica/" + dottoreRequest.getCodiceDottore()).retrieve()
				.bodyToMono(DottoreRequestDTO.class).block();

		if (result == null) {
			throw new RuntimeException();
		}

		ResponseEntity<DottoreRequestDTO> response = webClient.post().uri("/impostaInVisita")
				.body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve().toEntity(DottoreRequestDTO.class)
				.block();

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new RuntimeException();
		}

		LOGGER.info("....invocazione servizio esterno terminata....");
		service.assegnaDottore(id, dottoreRequest.getCodiceDottore());
	}

	@GetMapping("/ricovera/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ricovera(@PathVariable(required = true) Long id) {
		Paziente paziente = service.caricaSingoloElemento(id);
		if (paziente == null) {
			throw new NotFoundException("Paziente inesistente");
		}
		if (paziente.getCodiceDottore().isBlank()) {
			throw new PazienteSenzaDottoreException("Questo paziente non ha dei dottori");
		}
		DottoreRequestDTO dottoreRequest = new DottoreRequestDTO(paziente.getCodiceFiscale(),
				paziente.getCodiceDottore());

		LOGGER.info("....invocazione servizio esterno....");
		webClient.post().uri("/terminaVisita").body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve()
				.toEntity(DottoreRequestDTO.class).block();

		LOGGER.info("....invocazione servizio esterno terminata....");
		
		service.ricovera(paziente);
	}
	
	@GetMapping("/dimetti/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void dimetti(@PathVariable(required = true) Long id) {

		Paziente paziente = service.caricaSingoloElemento(id);
		if (paziente == null) {
			throw new NotFoundException("Paziente not found con id: " + id);
		}

		if (paziente.getCodiceDottore() == null) {
			throw new PazienteSenzaDottoreException("Impossibile procedere perche' il paziente non ha un dottore");
		}

		DottoreRequestDTO dottoreRequest = new DottoreRequestDTO(paziente.getCodiceFiscale(),
				paziente.getCodiceDottore());

		LOGGER.info("....invocazione servizio esterno....");
		webClient.post().uri("/terminaVisita").body(Mono.just(dottoreRequest), DottoreRequestDTO.class).retrieve()
				.toEntity(DottoreRequestDTO.class).block();
		LOGGER.info("....invocazione servizio esterno terminata....");
		
		service.dimetti(paziente);
	}
}
