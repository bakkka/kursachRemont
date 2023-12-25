package artemzenkov.kursach_remont.service;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.Node;
import artemzenkov.kursach_remont.domain.RepairedProduct;
import artemzenkov.kursach_remont.domain.Workpiece;
import artemzenkov.kursach_remont.model.DetailDTO;
import artemzenkov.kursach_remont.repos.DetailRepository;
import artemzenkov.kursach_remont.repos.NodeRepository;
import artemzenkov.kursach_remont.repos.RepairedProductRepository;
import artemzenkov.kursach_remont.repos.WorkpieceRepository;
import artemzenkov.kursach_remont.util.NotFoundException;
import artemzenkov.kursach_remont.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class DetailService {

    private final DetailRepository detailRepository;
    private final NodeRepository nodeRepository;
    private final RepairedProductRepository repairedProductRepository;
    private final WorkpieceRepository workpieceRepository;

    public DetailService(final DetailRepository detailRepository,
            final NodeRepository nodeRepository,
            final RepairedProductRepository repairedProductRepository,
            final WorkpieceRepository workpieceRepository) {
        this.detailRepository = detailRepository;
        this.nodeRepository = nodeRepository;
        this.repairedProductRepository = repairedProductRepository;
        this.workpieceRepository = workpieceRepository;
    }

    public List<DetailDTO> findAll() {
        final List<Detail> details = detailRepository.findAll(Sort.by("id"));
        return details.stream()
                .map(detail -> mapToDTO(detail, new DetailDTO()))
                .toList();
    }

    public DetailDTO get(final Long id) {
        return detailRepository.findById(id)
                .map(detail -> mapToDTO(detail, new DetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DetailDTO detailDTO) {
        final Detail detail = new Detail();
        mapToEntity(detailDTO, detail);
        return detailRepository.save(detail).getId();
    }

    public void update(final Long id, final DetailDTO detailDTO) {
        final Detail detail = detailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(detailDTO, detail);
        detailRepository.save(detail);
    }

    public void delete(final Long id) {
        detailRepository.deleteById(id);
    }

    private DetailDTO mapToDTO(final Detail detail, final DetailDTO detailDTO) {
        detailDTO.setId(detail.getId());
        detailDTO.setName(detail.getName());
        detailDTO.setType(detail.getType());
        detailDTO.setMaterial(detail.getMaterial());
        detailDTO.setNode(detail.getNode() == null ? null : detail.getNode().getId());
        detailDTO.setRepairedProducts(detail.getRepairedProducts().stream()
                .map(repairedProduct -> repairedProduct.getName())
                .toList());
        return detailDTO;
    }

    private Detail mapToEntity(final DetailDTO detailDTO, final Detail detail) {
        detail.setName(detailDTO.getName());
        detail.setType(detailDTO.getType());
        detail.setMaterial(detailDTO.getMaterial());
        final Node node = detailDTO.getNode() == null ? null : nodeRepository.findById(detailDTO.getNode())
                .orElseThrow(() -> new NotFoundException("node not found"));
        detail.setNode(node);
        final List<RepairedProduct> repairedProducts = repairedProductRepository.findAllById(
                detailDTO.getRepairedProducts() == null ? Collections.emptyList() : detailDTO.getRepairedProducts());
        if (repairedProducts.size() != (detailDTO.getRepairedProducts() == null ? 0 : detailDTO.getRepairedProducts().size())) {
            throw new NotFoundException("one of repairedProducts not found");
        }
        detail.setRepairedProducts(repairedProducts.stream().collect(Collectors.toSet()));
        return detail;
    }

    public String getReferencedWarning(final Long id) {
        final Detail detail = detailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Workpiece detailWorkpiece = workpieceRepository.findFirstByDetail(detail);
        if (detailWorkpiece != null) {
            return WebUtils.getMessage("detail.workpiece.detail.referenced", detailWorkpiece.getId());
        }
        return null;
    }

}
