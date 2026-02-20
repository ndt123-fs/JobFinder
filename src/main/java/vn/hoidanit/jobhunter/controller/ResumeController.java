package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.SecurityUtil;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@Validated
public class ResumeController {
    @Autowired
    private final FilterSpecificationConverter filterSpecificationConverter;
    @Autowired
    private final FilterBuilder filterBuilder;
    private ResumeService resumeService;
    private JobService jobService;
    private UserService userService;

    public ResumeController(ResumeService resumeService, JobService jobService, UserService userService, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.jobService = jobService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume !")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        User user = this.userService.handleGetUserById(resume.getUser().getId());
        Job job = this.jobService.handleGetJobById(resume.getJob().getId());
        if (job == null || user == null) {
            throw new IdInvalidException("Job id/ user id khong ton tai !");
        }
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.resumeService.convertResResumeDTO(this.resumeService.handleCreateResume(resume)));


    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume !")
    public ResponseEntity<ResUpdateResumDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Resume resumes = this.resumeService.getResumeById(resume.getId());
        if (resumes == null) {
            throw new IdInvalidException("Resume voi id " + resume.getId() + " khong ton tai !");
        }
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.resumeService.convertResAndUpdateDTO(resume));


    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("delete a resume !")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Id :" + id + "khong ton tai !");
        }
        this.resumeService.deleteResume(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume !")
    public ResponseEntity<ResResumeDTO> getAResume(@PathVariable("id") long id) throws IdInvalidException {
        Resume resume = this.resumeService.getResumeById(id);
        if (resume == null) {
            throw new IdInvalidException("Id :" + id + " khong ton tai !");
        }
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.resumeService.convertResumeDTO(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resume !")
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
       //filer Resume by Company (logic : tu user -> company_id->Job->job_id)
        // Lọc resume theo các job thuộc cùng 1 công ty của user đăng login

        List<Long> arrJobIds = new ArrayList<>();
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        final List<Long> fArrJobIds = arrJobIds;
        Specification<Resume> jobInSpec = (root, query, criteriaBuilder) -> {
            return criteriaBuilder.in(root.get("job").get("id")).value(fArrJobIds);
        };

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));

    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("get list resumes by users !")
    public ResponseEntity<ResultPaginationDTO> getResumeByUserLogin(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
