package org.ps.dummy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalUserListDTO {
    private List<ExternalUserDTO> users;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
