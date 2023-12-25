package artemzenkov.kursach_remont.repos;

import artemzenkov.kursach_remont.domain.RepairedProduct;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RepairedProductRepository extends JpaRepository<RepairedProduct, Long> {
}
