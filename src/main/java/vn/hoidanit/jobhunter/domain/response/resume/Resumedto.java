package vn.hoidanit.jobhunter.domain.response.resume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resumedto {
    private long id;
    private String email;
    private String url;
    private String status;
    private String companyName;
}
