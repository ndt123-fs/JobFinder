package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCompanyDTO {
    private long id;
    private String name;
    private String address;
    private String logo;
    private Instant createdAt;
    private Instant updatedAt;

}
