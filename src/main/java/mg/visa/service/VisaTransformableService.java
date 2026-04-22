package mg.visa.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mg.visa.dto.VisaTransformableDTO;
import mg.visa.entity.Passeport;
import mg.visa.entity.VisaTransformable;
import mg.visa.entity.ref.TypeVisa;
import mg.visa.repository.PasseportRepository;
import mg.visa.repository.VisaTransformableRepository;
import mg.visa.repository.ref.TypeVisaRepository;

@Service
public class VisaTransformableService {

    private final VisaTransformableRepository vtRepository;
    private final PasseportRepository passeportRepository;
    private final TypeVisaRepository typeVisaRepository;

    public VisaTransformableService(VisaTransformableRepository vtRepository,
                                    PasseportRepository passeportRepository,
                                    TypeVisaRepository typeVisaRepository) {
        this.vtRepository = vtRepository;
        this.passeportRepository = passeportRepository;
        this.typeVisaRepository = typeVisaRepository;
    }

    public VisaTransformable create(VisaTransformableDTO dto) {
        VisaTransformable vt = new VisaTransformable();

        if (dto.getPasseportId() != null) {
            Passeport p = passeportRepository.findById(dto.getPasseportId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "passeport introuvable"));
            vt.setPasseport(p);
        }

        if (dto.getTypeVisaId() != null) {
            TypeVisa tv = typeVisaRepository.findById(dto.getTypeVisaId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "type visa introuvable"));
            vt.setTypeVisa(tv);
        }

        vt.setInfos(dto.getInfos());
        vt.setRemarque(dto.getRemarque());

        return vtRepository.save(vt);
    }
}
