package mg.visa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.dto.DemandeurDTO;
import mg.visa.entity.Demandeur;
import mg.visa.service.DemandeurService;

@RestController
@RequestMapping("/api/demandeurs")
public class DemandeurController {

    private final DemandeurService demandeurService;

    public DemandeurController(DemandeurService demandeurService) {
        this.demandeurService = demandeurService;
    }

    @PostMapping
    public ResponseEntity<Demandeur> create(@RequestBody DemandeurDTO dto) {
        Demandeur created = demandeurService.createDemandeur(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(demandeurService.getAllDemandeurs());
    }
}
