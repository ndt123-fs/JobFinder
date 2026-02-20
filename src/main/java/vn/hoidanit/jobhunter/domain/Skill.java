package vn.hoidanit.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.service.SecurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id ;
    @NotBlank(message = "name khong duoc de trong !")
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
   // dinh nghia mapped ben obj nao , thi joinTable ben doi tuong con lai (N-N)
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "skills")
    @JsonIgnore // nho de annotaion nay ma khi truy van k bi vong lap vo han , co the la an field do luon
    private List<Job> jobs;
    //
    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "skills")
    @JsonIgnore // dung chi de bo dung 1 field
    private List<Subscriber> subscribers;



    @PrePersist
    public void handleBeforeCreate(){
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate(){
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        this.updatedAt = Instant.now();
    }

}
