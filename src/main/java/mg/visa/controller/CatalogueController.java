package mg.visa.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mg.visa.entity.CataloguePieceCommune;
import mg.visa.entity.CataloguePieceComplementaire;
import mg.visa.entity.ref.TypeDemande;
import mg.visa.entity.ref.TypeVisa;
import mg.visa.repository.CataloguePieceCommuneRepository;
import mg.visa.repository.CataloguePieceComplementaireRepository;
import mg.visa.repository.ref.TypeDemandeRepository;
import mg.visa.repository.ref.TypeVisaRepository;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final CataloguePieceCommuneRepository communeRepo;
    private final CataloguePieceComplementaireRepository compRepo;
    private final TypeDemandeRepository typeDemandeRepo;
    private final TypeVisaRepository typeVisaRepo;

    public CatalogueController(CataloguePieceCommuneRepository communeRepo,
            CataloguePieceComplementaireRepository compRepo,
            TypeDemandeRepository typeDemandeRepo,
            TypeVisaRepository typeVisaRepo) {
        this.communeRepo = communeRepo;
        this.compRepo = compRepo;
        this.typeDemandeRepo = typeDemandeRepo;
        this.typeVisaRepo = typeVisaRepo;
    }

    @GetMapping("/communes")
    public List<CataloguePieceCommune> communes() {
        return communeRepo.findAll();
    }

    @GetMapping("/complementaires")
    public List<CataloguePieceComplementaire> complementaires(@RequestParam(value = "typeDemandeId", required = false) Long typeDemandeId) {
        if (typeDemandeId == null) return compRepo.findAll();

        TypeDemande td = typeDemandeRepo.findById(typeDemandeId).orElse(null);
        if (td == null) return java.util.Collections.emptyList();

        TypeVisa tv = typeVisaRepo.findByCode(td.getCode()).orElse(null);
        if (tv == null) return java.util.Collections.emptyList();

        return compRepo.findByTypeVisaId(tv.getId());
    }
}
