package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;


public interface JobService {
    Job handleCreateJob(Job job);

    ResCreateJobDTO convertToResCreateJobDTO(Job job);

    Job handleUpdateJob(Job job,Job jobInDB);

    Job handleGetJobById(Long id);

    ResUpdateJobDTO convertToResUpdateJobDTO(Job job);

    ResultPaginationDTO handleGetAllJob(Specification<Job> spec, Pageable pageable);
    void handleDeleteJob(Long id);
}
