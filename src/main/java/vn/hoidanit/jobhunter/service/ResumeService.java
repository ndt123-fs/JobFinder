package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumDTO;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

public interface ResumeService {
    Resume handleCreateResume(Resume resume);

    ResCreateResumeDTO convertResResumeDTO(Resume resume);

    Resume getResumeById(Long id);

    ResUpdateResumDTO convertResAndUpdateDTO(Resume resume) throws IdInvalidException;

    void deleteResume(Long id);

    ResultPaginationDTO getAllResume(Specification<Resume> spec, Pageable pageable);


    ResResumeDTO convertResumeDTO(Resume resume) ;


    ResultPaginationDTO fetchResumeByUser(Pageable pageable);
}
