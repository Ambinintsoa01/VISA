package mg.visa.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.dto.PasseportDTO;
import mg.visa.entity.Demandeur;
import mg.visa.entity.Passeport;
import mg.visa.repository.DemandeurRepository;
import mg.visa.repository.PasseportRepository;

@Service
public class PasseportService {

    private final PasseportRepository passeportRepository;
    private final DemandeurRepository demandeurRepository;

    public PasseportService(PasseportRepository passeportRepository, DemandeurRepository demandeurRepository) {
        this.passeportRepository = passeportRepository;
        this.demandeurRepository = demandeurRepository;
    }

    public Passeport createPasseport(PasseportDTO dto) {
        if (dto.getNumero() == null || dto.getNumero().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "numero passeport requis");
        }

        Demandeur demandeur = demandeurRepository.findById(dto.getDemandeurId())
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "demandeur introuvable"));

        Passeport p = new Passeport();
        p.setNumero(dto.getNumero());
        p.setPaysEmission(dto.getPaysEmission());
        p.setDateEmission(dto.getDateEmission());
        p.setDateExpiration(dto.getDateExpiration());
        p.setDemandeur(demandeur);

        return passeportRepository.save(p);
    }
}
