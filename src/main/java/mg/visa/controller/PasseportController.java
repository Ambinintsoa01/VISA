package mg.visa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.dto.PasseportDTO;
import mg.visa.entity.Passeport;
import mg.visa.service.PasseportService;

@RestController
@RequestMapping("/api/passeports")
public class PasseportController {

    private final PasseportService passeportService;

    public PasseportController(PasseportService passeportService) {
        this.passeportService = passeportService;
    }

    @PostMapping
    public ResponseEntity<Passeport> create(@RequestBody PasseportDTO dto) {
        Passeport created = passeportService.createPasseport(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
