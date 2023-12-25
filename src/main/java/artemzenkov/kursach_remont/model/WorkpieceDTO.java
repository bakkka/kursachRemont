package artemzenkov.kursach_remont.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WorkpieceDTO {

    private Long id;

    @Size(max = 255)
    private String type;

    private Integer productionTime;

    private Double consumption;

    private Long detail;

}
