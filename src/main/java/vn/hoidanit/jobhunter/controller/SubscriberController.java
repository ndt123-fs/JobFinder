package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SecurityUtil;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;
import vn.hoidanit.jobhunter.utils.error.EmailInvalidException;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")

public class SubscriberController {
    private final SubscriberService subscriberService;



    public SubscriberController(SubscriberService subscriberService, EmailService emailService) {
        this.subscriberService = subscriberService;

    }
    @Scheduled(cron = "0 0 7 * * ?") //
    @Transactional// 8h sáng mỗi ngày
    public void sendJobAlert() {
        System.out.println("Send cron mail");
       this.subscriberService.sendSubscribersEmailJobs();
    }
    @PostMapping("/subscribers")
    @ApiMessage("Create subscribers !")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws EmailInvalidException, IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.subscriberService.handleCreateSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update subscribers !")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriberRq) throws IdInvalidException {
        Subscriber subDB = this.subscriberService.fetchSubscriberById(subscriberRq.getId());
        if (subDB == null) {
            throw new IdInvalidException("Id " + subscriberRq.getId() + " khong ton tai !");
        }

        return ResponseEntity.status(HttpStatus.OK.value()).body(this.subscriberService.handleUpdateSubscriber(subscriberRq, subDB));

    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get's subscribers !")
    public ResponseEntity<Subscriber> getSubscriberSkills() throws EmailInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        System.out.println(this.subscriberService.findByEmails(email));

        return ResponseEntity.status(HttpStatus.OK.value()).body(this.subscriberService.findByEmails(email));
    }

}
