package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@Validated
@RequestMapping("/api/v1")
public class JobController {
    private JobService jobService;
    private SkillService skillService;

    public JobController(JobService jobService, SkillService skillService) {
        this.jobService = jobService;
        this.skillService = skillService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job !")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
        //skill -> skill.getId() = skill :: getId()

        ResCreateJobDTO cvtJob = this.jobService.convertToResCreateJobDTO(this.jobService.handleCreateJob(job));
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(cvtJob);
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job!")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
        Job checkJob = this.jobService.handleGetJobById(job.getId());
        if (checkJob == null) {
            throw new IdInvalidException("Job voi Id : " + job.getId() + " khong ton tai , vui long nhap lai !");
        }
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.jobService.convertToResUpdateJobDTO(this.jobService.handleUpdateJob(job,checkJob)));
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all jobs with pagination !")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Valid @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(this.jobService.handleGetAllJob(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get job by id !")
    public ResponseEntity<Job> getJobById(@Valid @PathVariable("id") long id) throws IdInvalidException {
        Job job = this.jobService.handleGetJobById(id);
        if (job == null) {
            throw new IdInvalidException("Job voi Id: " + id + " không tồn tại !");
        } else
            return ResponseEntity.status(HttpStatus.OK.value()).body(job);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job !")
    public ResponseEntity<Void> deleteJob(@Valid @PathVariable("id") long id) throws IdInvalidException {
        Job job = this.jobService.handleGetJobById(id);
        if (job == null) {
            throw new IdInvalidException("Không có Job với Id : " + id);
        } else return ResponseEntity.status(HttpStatus.OK.value()).body(null);

    }

}
