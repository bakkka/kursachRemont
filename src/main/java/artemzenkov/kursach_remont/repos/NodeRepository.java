package artemzenkov.kursach_remont.repos;

import artemzenkov.kursach_remont.domain.Node;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NodeRepository extends JpaRepository<Node, Long> {
}
