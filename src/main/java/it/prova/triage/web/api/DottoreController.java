package it.prova.triage.web.api;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage.dto.DottoreResponseDTO;

@RestController
@RequestMapping("/api/dottore")
public class DottoreController {

	@Autowired
	private WebClient webClient;

	@GetMapping
	public List<DottoreResponseDTO> listAll() {
		List<DottoreResponseDTO> result = (List<DottoreResponseDTO>) webClient.get().uri("/").retrieve()
				.bodyToFlux(DottoreResponseDTO.class).buffer().blockLast();
		return result;
	}

}
