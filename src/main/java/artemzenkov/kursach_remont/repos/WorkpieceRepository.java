package artemzenkov.kursach_remont.repos;

import artemzenkov.kursach_remont.domain.Detail;
import artemzenkov.kursach_remont.domain.Workpiece;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WorkpieceRepository extends JpaRepository<Workpiece, Long> {

    Workpiece findFirstByDetail(Detail detail);

}
