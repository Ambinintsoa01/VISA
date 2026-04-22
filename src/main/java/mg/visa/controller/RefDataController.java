package mg.visa.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.entity.ref.Nationalite;
import mg.visa.entity.ref.SituationFamiliale;
import mg.visa.entity.ref.TypeIdentite;
import mg.visa.service.RefDataService;

@RestController
@RequestMapping("/api/ref")
public class RefDataController {

    private final RefDataService refDataService;

    public RefDataController(RefDataService refDataService) {
        this.refDataService = refDataService;
    }

    @GetMapping("/nationalites")
    public List<Nationalite> nationalites() {
        return refDataService.getNationalites();
    }

    @GetMapping("/situations-familiales")
    public List<SituationFamiliale> situationsFamiliales() {
        return refDataService.getSituationsFamiliales();
    }

    @GetMapping("/types-identite")
    public List<TypeIdentite> typesIdentite() {
        return refDataService.getTypesIdentite();
    }
}
