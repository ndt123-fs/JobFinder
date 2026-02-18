package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.*;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.JobService;

import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {
    private JobRepository jobRepository;
    private SkillRepository skillRepository;
    private CompanyRepository companyRepository;

    public JobServiceImpl(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Job handleCreateJob(Job job) {
        //check skill
        if (job.getSkills() != null) {
            List<Skill> lstSkill = this.skillRepository.findByIdIn(job.getSkills().stream().map(Skill::getId).toList());
            job.setSkills(lstSkill);
        }
        //checkcompany
        if(job.getCompany() != null){
            Optional<Company> cOptional = this.companyRepository.findById(job.getId());
            cOptional.ifPresent(job::setCompany);
        }

        return this.jobRepository.save(job);
    }

    @Override
    public ResCreateJobDTO convertToResCreateJobDTO(Job job) {
        ResCreateJobDTO convertJob = new ResCreateJobDTO();

        convertJob.setId(job.getId());
        convertJob.setName(job.getName());
        convertJob.setLocation(job.getLocation());
        convertJob.setSalary(job.getSalary());
        convertJob.setQuantity(job.getQuantity());
        convertJob.setLevel(job.getLevel());
        convertJob.setStartDate(job.getStartDate());
        convertJob.setEndDate(job.getEndDate());
        convertJob.setSkills(job.getSkills().stream().map(Skill::getName).toList());
        convertJob.setCreatedAt(job.getCreatedAt());
        convertJob.setCreatedBy(job.getCreatedBy());
        convertJob.setActive(job.isActive());
        return convertJob;
    }

    @Override
    public ResUpdateJobDTO convertToResUpdateJobDTO(Job job) {
        ResUpdateJobDTO convertJob = new ResUpdateJobDTO();

        convertJob.setId(job.getId());
        convertJob.setName(job.getName());
        convertJob.setLocation(job.getLocation());
        convertJob.setSalary(job.getSalary());
        convertJob.setQuantity(job.getQuantity());
        convertJob.setLevel(job.getLevel());
        convertJob.setStartDate(job.getStartDate());
        convertJob.setEndDate(job.getEndDate());
        convertJob.setSkills(job.getSkills().stream().map(Skill::getName).toList());
        convertJob.setUpdatedAt(job.getUpdatedAt());
        convertJob.setUpdatedBy(job.getUpdatedBy());
        convertJob.setActive(job.isActive());
        return convertJob;
    }

    @Override
    public Job handleGetJobById(Long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.orElse(null);
    }

    @Override
    public Job handleUpdateJob(Job job, Job jobInDB) {

        // checkSkill
        if (job.getSkills() != null) {
            // lấy ra danh sách id
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            // cập nhật jobInDB trước ví khi update nếu người dùng quên nhập thì nó lấy ở database ghi đè lên
            jobInDB.setSkills(dbSkills);
        }
        // checkCompany
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            if (cOptional.isPresent()) {
                jobInDB.setCompany(cOptional.get());
            }
        }
        //update info
        jobInDB.setName(job.getName());
        jobInDB.setSalary(job.getSalary());
        jobInDB.setQuantity(job.getQuantity());
        jobInDB.setLocation(job.getLocation());
        jobInDB.setLevel(job.getLevel());
        jobInDB.setStartDate(job.getStartDate());
        jobInDB.setEndDate(job.getEndDate());
        jobInDB.setActive(job.isActive());
        // lưu xuống
        return this.jobRepository.save(jobInDB);
    }

    @Override
    public ResultPaginationDTO handleGetAllJob(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();

        meta.setPage(pageable.getPageNumber() + 1); // trang hien tai
        meta.setPageSize(pageable.getPageSize());// trang nay co bn nhieu phan tu

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageJob.getContent());

        return result;


    }

    @Override
    public void handleDeleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }


}
