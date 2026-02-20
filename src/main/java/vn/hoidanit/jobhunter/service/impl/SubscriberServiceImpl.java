package vn.hoidanit.jobhunter.service.impl;

import jakarta.validation.constraints.Email;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.utils.error.EmailInvalidException;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriberServiceImpl implements SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    @Override
    public Subscriber handleCreateSubscriber(Subscriber subscriber) throws EmailInvalidException, IdInvalidException {
        boolean checkMail = this.subscriberRepository.existsByEmail(subscriber.getEmail());
        if (checkMail) {
            throw new EmailInvalidException("Email :" + subscriber.getEmail() + " da ton tai !");
        }
        //check skill
        if (subscriber.getSkills() != null) {
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
    public Subscriber handleUpdateSubscriber(Subscriber subscriberRq, Subscriber subscriberDb) throws IdInvalidException {
        if (subscriberRq.getSkills() != null) {
            List<Long> reqSkills = subscriberRq.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriberDb.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriberDb);
    }

    @Override
    public Subscriber fetchSubscriberById(long id) {
        Optional<Subscriber> subOptional = this.subscriberRepository.findById(id);
        return subOptional.orElse(null);
    }

    @Override

    public void sendSubscribersEmailJobs() {
        // trong thuc te k nen lay findAll() , dung phan trang
        List<Subscriber> lstSub = this.subscriberRepository.findAll();
        if (lstSub != null && lstSub.size() > 0) {
            for (Subscriber sub : lstSub) {
                List<Skill> lstSkill = sub.getSkills();
                if (lstSkill != null && lstSkill.size() > 0) {
                    List<Job> lstJob = this.jobRepository.findBySkillsIn(lstSkill);
                    if (lstJob != null && lstJob.size() > 0) {
                        List<ResEmailJob> arr = lstJob.stream().map(item -> this.convertJobToSendEmail(item)).toList();
                        this.emailService.sendMailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }

                }

            }
        }
    }

    @Override
    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill-> new ResEmailJob.SkillEmail(skill.getName())).toList();
        res.setSkills(s);
        return res;
    }

}


