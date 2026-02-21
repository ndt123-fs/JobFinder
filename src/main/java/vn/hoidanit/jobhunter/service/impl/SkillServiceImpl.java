package vn.hoidanit.jobhunter.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.skill.ResDTOGetListSkill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.SkillService;

import java.util.List;
import java.util.Optional;

@Service
public class SkillServiceImpl implements SkillService {
    private SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);

    }

    @Override
    public boolean checkNameSkill(String name) {
        return this.skillRepository.existsByName(name);
    }

    @Override
    public Skill checkId(long id) {
        Optional<Skill> check = this.skillRepository.findById(id);
        if (check.isPresent()) {
            return check.get();
        }
        return null;
    }

    @Override
    public Skill handleUpdateSkill(Skill skill) {
        Optional<Skill> check = this.skillRepository.findById(skill.getId());
        if (check.isPresent()) {
            Skill updateSkill = check.get();
            updateSkill.setName(skill.getName());
            this.skillRepository.save(updateSkill);
            return updateSkill;

        }
        return null;
    }

    @Override
    public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        result.setMeta(meta);

        result.setResult(pageSkill.getContent());


        return result;
    }

    public void handleDeleteSkill(long id) {
        // delete skill trong jobs
        Optional<Skill> skill = this.skillRepository.findById(id);
        Skill currentSkill = skill.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        //
        currentSkill.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(currentSkill));
        // delete skill
        this.skillRepository.delete(currentSkill);
    }
@Override
    public List<Skill> findListSkillById(List<Long> id) {

        return this.skillRepository.findByIdIn(id);
    }
@Override
    public ResDTOGetListSkill convertTo(Skill skill) {
        ResDTOGetListSkill aSkill = new ResDTOGetListSkill();
        aSkill.setName(skill.getName());
        return aSkill;
    }


}
