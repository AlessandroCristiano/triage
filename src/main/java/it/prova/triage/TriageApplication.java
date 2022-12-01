package it.prova.triage;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.triage.model.Ruolo;
import it.prova.triage.model.Utente;
import it.prova.triage.service.RuoloService;
import it.prova.triage.service.UtenteService;

@SpringBootApplication
public class TriageApplication implements CommandLineRunner{
	
	private static final Logger LOGGER = LogManager.getLogger(TriageApplication.class);
	
	@Autowired
	private RuoloService ruoloServiceInstance;
	
	@Autowired
	private UtenteService utenteServiceInstance;

	public static void main(String[] args) {
		SpringApplication.run(TriageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", Ruolo.ROLE_ADMIN));
		}

		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Operator", Ruolo.ROLE_SUB_OPERATOR) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Operator", Ruolo.ROLE_SUB_OPERATOR));
		}
		
		if (utenteServiceInstance.findByUsername("admin") == null) {
			Utente admin = new Utente("admin", "admin", "Mario", "Rossi", LocalDate.now());
			admin.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN));
			utenteServiceInstance.inserisciNuovo(admin);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(admin.getId());
		}
		
		if (utenteServiceInstance.findByUsername("user") == null) {
			Utente user = new Utente("user", "user", "Armando", "Casamonica", LocalDate.now());
			user.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Operator", Ruolo.ROLE_SUB_OPERATOR));
			utenteServiceInstance.inserisciNuovo(user);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(user.getId());
		}
		
	}

}
