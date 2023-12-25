package artemzenkov.kursach_remont.repos;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.Node;
import artemzenkov.kursach_remont.domain.RepairedProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DetailRepository extends JpaRepository<Detail, Long> {

    Detail findFirstByNode(Node node);

    Detail findFirstByRepairedProducts(RepairedProduct repairedProduct);

    List<Detail> findAllByRepairedProducts(RepairedProduct repairedProduct);

}
