package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.skill.ResDTOGetListSkill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;

import java.util.List;

public interface SkillService {
    Skill handleCreateSkill(Skill skill);

    boolean checkNameSkill(String name);

    Skill handleUpdateSkill(Skill skill);

    ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable);

    Skill checkId(long id);

    void handleDeleteSkill(long id);

    List<Skill> findListSkillById(List<Long> id);

    ResDTOGetListSkill convertTo(Skill skill);


}
