package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailNotificationController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailNotificationController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/emails")
    @ApiMessage("Send simple email !")
//    @Scheduled(cron = "*/10 * * * * *")
//    @Transactional
    public ResponseEntity<String> sendSimpleEmail() {
      //  this.emailService.sendSimpleEmail();
       // this.emailService.sendMailAsync("nduytin13112005@gmail.com","TEST","<b>HELLO<b>",false,true);t
      //  this.emailService.sendMailFromTemplateSync("nduytin13112005@gmail.com","Test","job",);
        this.subscriberService.sendSubscribersEmailJobs();

        return ResponseEntity.status(HttpStatus.OK.value()).body("Send email succesfull !");
    }
}
