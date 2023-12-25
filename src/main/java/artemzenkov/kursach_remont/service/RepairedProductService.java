package artemzenkov.kursach_remont.service;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.RepairedProduct;
import artemzenkov.kursach_remont.model.RepairedProductDTO;
import artemzenkov.kursach_remont.repos.DetailRepository;
import artemzenkov.kursach_remont.repos.RepairedProductRepository;
import artemzenkov.kursach_remont.util.NotFoundException;
import artemzenkov.kursach_remont.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class RepairedProductService {

    private final RepairedProductRepository repairedProductRepository;
    private final DetailRepository detailRepository;

    public RepairedProductService(final RepairedProductRepository repairedProductRepository,
            final DetailRepository detailRepository) {
        this.repairedProductRepository = repairedProductRepository;
        this.detailRepository = detailRepository;
    }

    public List<RepairedProductDTO> findAll() {
        final List<RepairedProduct> repairedProducts = repairedProductRepository.findAll(Sort.by("name"));
        return repairedProducts.stream()
                .map(repairedProduct -> mapToDTO(repairedProduct, new RepairedProductDTO()))
                .toList();
    }

    public RepairedProductDTO get(final Long name) {
        return repairedProductRepository.findById(name)
                .map(repairedProduct -> mapToDTO(repairedProduct, new RepairedProductDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final RepairedProductDTO repairedProductDTO) {
        final RepairedProduct repairedProduct = new RepairedProduct();
        mapToEntity(repairedProductDTO, repairedProduct);
        return repairedProductRepository.save(repairedProduct).getName();
    }

    public void update(final Long name, final RepairedProductDTO repairedProductDTO) {
        final RepairedProduct repairedProduct = repairedProductRepository.findById(name)
                .orElseThrow(NotFoundException::new);
        mapToEntity(repairedProductDTO, repairedProduct);
        repairedProductRepository.save(repairedProduct);
    }

    public void delete(final Long name) {
        final RepairedProduct repairedProduct = repairedProductRepository.findById(name)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        detailRepository.findAllByRepairedProducts(repairedProduct)
                .forEach(detail -> detail.getRepairedProducts().remove(repairedProduct));
        repairedProductRepository.delete(repairedProduct);
    }

    private RepairedProductDTO mapToDTO(final RepairedProduct repairedProduct,
            final RepairedProductDTO repairedProductDTO) {
        repairedProductDTO.setName(repairedProduct.getName());
        repairedProductDTO.setActualDepreciation(repairedProduct.getActualDepreciation());
        return repairedProductDTO;
    }

    private RepairedProduct mapToEntity(final RepairedProductDTO repairedProductDTO,
            final RepairedProduct repairedProduct) {
        repairedProduct.setActualDepreciation(repairedProductDTO.getActualDepreciation());
        return repairedProduct;
    }

    public String getReferencedWarning(final Long name) {
        final RepairedProduct repairedProduct = repairedProductRepository.findById(name)
                .orElseThrow(NotFoundException::new);
        final Detail repairedProductsDetail = detailRepository.findFirstByRepairedProducts(repairedProduct);
        if (repairedProductsDetail != null) {
            return WebUtils.getMessage("repairedProduct.detail.repairedProducts.referenced", repairedProductsDetail.getId());
        }
        return null;
    }

}
