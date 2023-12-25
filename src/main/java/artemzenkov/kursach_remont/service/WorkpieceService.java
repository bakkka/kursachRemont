package artemzenkov.kursach_remont.service;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.Workpiece;
import artemzenkov.kursach_remont.model.WorkpieceDTO;
import artemzenkov.kursach_remont.repos.DetailRepository;
import artemzenkov.kursach_remont.repos.WorkpieceRepository;
import artemzenkov.kursach_remont.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class WorkpieceService {

    private final WorkpieceRepository workpieceRepository;
    private final DetailRepository detailRepository;

    public WorkpieceService(final WorkpieceRepository workpieceRepository,
            final DetailRepository detailRepository) {
        this.workpieceRepository = workpieceRepository;
        this.detailRepository = detailRepository;
    }

    public List<WorkpieceDTO> findAll() {
        final List<Workpiece> workpieces = workpieceRepository.findAll(Sort.by("id"));
        return workpieces.stream()
                .map(workpiece -> mapToDTO(workpiece, new WorkpieceDTO()))
                .toList();
    }

    public WorkpieceDTO get(final Long id) {
        return workpieceRepository.findById(id)
                .map(workpiece -> mapToDTO(workpiece, new WorkpieceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final WorkpieceDTO workpieceDTO) {
        final Workpiece workpiece = new Workpiece();
        mapToEntity(workpieceDTO, workpiece);
        return workpieceRepository.save(workpiece).getId();
    }

    public void update(final Long id, final WorkpieceDTO workpieceDTO) {
        final Workpiece workpiece = workpieceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(workpieceDTO, workpiece);
        workpieceRepository.save(workpiece);
    }

    public void delete(final Long id) {
        workpieceRepository.deleteById(id);
    }

    private WorkpieceDTO mapToDTO(final Workpiece workpiece, final WorkpieceDTO workpieceDTO) {
        workpieceDTO.setId(workpiece.getId());
        workpieceDTO.setType(workpiece.getType());
        workpieceDTO.setProductionTime(workpiece.getProductionTime());
        workpieceDTO.setConsumption(workpiece.getConsumption());
        workpieceDTO.setDetail(workpiece.getDetail() == null ? null : workpiece.getDetail().getId());
        return workpieceDTO;
    }

    private Workpiece mapToEntity(final WorkpieceDTO workpieceDTO, final Workpiece workpiece) {
        workpiece.setType(workpieceDTO.getType());
        workpiece.setProductionTime(workpieceDTO.getProductionTime());
        workpiece.setConsumption(workpieceDTO.getConsumption());
        final Detail detail = workpieceDTO.getDetail() == null ? null : detailRepository.findById(workpieceDTO.getDetail())
                .orElseThrow(() -> new NotFoundException("detail not found"));
        workpiece.setDetail(detail);
        return workpiece;
    }

}
