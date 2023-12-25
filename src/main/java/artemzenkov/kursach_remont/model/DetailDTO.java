package artemzenkov.kursach_remont.model;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DetailDTO {

    private Long id;

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String type;

    @Size(max = 255)
    private String material;

    private Long node;

    private List<Long> repairedProducts;

}
