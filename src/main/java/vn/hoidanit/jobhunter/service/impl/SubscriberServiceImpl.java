package vn.hoidanit.jobhunter.service.impl;

import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.utils.error.EmailInvalidException;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriberServiceImpl implements SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public Subscriber handleCreateSubscriber(Subscriber subscriber) throws EmailInvalidException, IdInvalidException {
        boolean checkMail = this.subscriberRepository.existsByEmail(subscriber.getEmail());
        if (checkMail) {
            throw new EmailInvalidException("Email :" + subscriber.getEmail() + " da ton tai !");
        }
        //check skill
        if(subscriber.getSkills() != null) {
            List<Long> idSkill = subscriber.getSkills().stream().map(Skill::getId).toList();
            List<Skill> checkSkill = this.skillRepository.findByIdIn(idSkill);
            if (checkSkill == null) {
                throw new IdInvalidException("Skill khong ton tai !");
            } else {
                subscriber.setSkills(checkSkill);
            }
        }
        return this.subscriberRepository.save(subscriber);

    }

    @Override
    public Subscriber handleUpdateSubscriber(  Subscriber subscriberRq, Subscriber subscriberDb) throws IdInvalidException {
    if(subscriberRq.getSkills() != null ){
        List<Long> reqSkills = subscriberRq.getSkills().stream().map(Skill::getId).toList();
        List<Skill>dbSkills = this.skillRepository.findByIdIn(reqSkills);
        subscriberDb.setSkills(dbSkills);
    }
    return this.subscriberRepository.save(subscriberDb);
    }
    @Override
    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> subOptional = this.subscriberRepository.findById(id);
        return subOptional.orElse(null);
    }

}
