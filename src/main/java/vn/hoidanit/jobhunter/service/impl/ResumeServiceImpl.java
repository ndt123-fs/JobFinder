package vn.hoidanit.jobhunter.service.impl;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.SecurityUtil;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeServiceImpl implements ResumeService {
    private ResumeRepository resumeRepository;
    private JobRepository jobRepository;
    private UserRepository userRepository;
    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;
    @Autowired
    private FilterParser filterParser;

    public ResumeServiceImpl(ResumeRepository resumeRepository, JobRepository jobRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Resume handleCreateResume(Resume resume) {
        return this.resumeRepository.save(resume);

    }

    public ResCreateResumeDTO convertResResumeDTO(Resume resume) {
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    @Override
    public Resume getResumeById(Long id) {
        Optional<Resume> check = this.resumeRepository.findById(id);
        return check.orElse(null);
    }

    @Override
    public void deleteResume(Long id) {
        this.resumeRepository.deleteById(id);
    }

    @Override
    public ResUpdateResumDTO convertResAndUpdateDTO(Resume resume) throws IdInvalidException {
        Resume resumeCheck = this.getResumeById(resume.getId());
        // trong cung 1 class thi dung this
        if (resumeCheck == null) {
            throw new IdInvalidException("Resume not found with id = " + resume.getId());
        }
        resumeCheck.setStatus(resume.getStatus());

        Resume saved = this.resumeRepository.save(resumeCheck);
        ResUpdateResumDTO res = new ResUpdateResumDTO();
        res.setUpdatedAt(saved.getUpdatedAt());
        res.setUpdatedBy(saved.getUpdatedBy());

        return res;

    }

    @Override
    public ResultPaginationDTO getAllResume(Specification<Resume> spec, Pageable pageable) {

        // lay ra user hien tai

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        //
        meta.setPages(pageResume.getTotalPages());
        meta.setTotal(pageResume.getTotalElements());
        //
        result.setMeta(meta);
        result.setResult(pageResume.getContent().stream().map(item -> this.convertResumeDTO(this.getResumeById(item.getId()))).toList());
        return result;

    }

    @Override
    public ResResumeDTO convertResumeDTO(Resume resume) {
        ResResumeDTO res = new ResResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        // nếu cv có công việc , thì lấy ngược lại lên cái job lấy tên company
        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        ResResumeDTO.UserResume uResume = new ResResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName());
        ResResumeDTO.JobResume jResume = new ResResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName());
        res.setUser(uResume);
        res.setJob(jResume);


        return res;

    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {

        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get() : "";

        FilterNode node = filterParser.parse("email= '" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);
//        pageResume.getContent().forEach(r -> {
//            System.out.println(r.getJob().getCompany().getName());
//        });
        rs.setResult(pageResume.getContent().stream().map(x -> new ResResumeDTO(
                x.getId(),
                x.getEmail(),
                x.getUrl(),
                x.getStatus(),
                x.getCreatedAt(),
                x.getUpdatedAt(),
                x.getCreatedBy(),
                x.getUpdatedBy(),
                x.getJob() != null ? x.getJob().getCompany().getName() : null,
                x.getUser() != null ? new ResResumeDTO.UserResume(x.getUser().getId(), x.getUser().getName()) : null,
                x.getJob() != null ? new ResResumeDTO.JobResume(x.getJob().getId(), x.getJob().getName()) : null
        )).collect(Collectors.toList()));

        return rs;
    }


}
