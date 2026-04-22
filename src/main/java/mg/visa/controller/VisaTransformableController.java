package mg.visa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.dto.VisaTransformableDTO;
import mg.visa.entity.VisaTransformable;
import mg.visa.service.VisaTransformableService;

@RestController
@RequestMapping("/api/visas")
public class VisaTransformableController {

    private final VisaTransformableService visaTransformableService;

    public VisaTransformableController(VisaTransformableService visaTransformableService) {
        this.visaTransformableService = visaTransformableService;
    }

    @PostMapping
    public ResponseEntity<VisaTransformable> create(@RequestBody VisaTransformableDTO dto) {
        VisaTransformable created = visaTransformableService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
