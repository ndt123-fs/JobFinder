package vn.hoidanit.jobhunter.service.impl;

import com.nimbusds.jose.util.StandardCharset;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.service.EmailService;

import java.util.Arrays;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JobRepository jobRepository;

    public EmailServiceImpl(MailSender mailSender, JavaMailSender javaMailSender,SpringTemplateEngine templateEngine,JobRepository jobRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobRepository = jobRepository;
    }

    @Override
    public void sendSimpleEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("2351050180tin@ou.edu.vn", " 2351050020duy@gmail.com", "nguyenduytintin028@gmail.com", "2351050178tien@gmail.com");
        msg.setSubject("Deadline spring boot !");
        msg.setText("My name is SUPER_TIN , dcm bay lo lam bai tap lon di nghe chua");
        this.mailSender.send(msg);

    }

    @Override
    public void sendMailAsync(String to, String subject, String content, boolean isMultipart, boolean isHTML) {
        // isMultipart : gui mail dinh kem file , hinh anh ,..
        // boolean isHTML : nghia la co the HTML,css cho mail
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {
            //MimeMessageHelper giup thao tac de dang hon so voi MimeMessage
            MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharset.UTF_8.name());
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(content, isHTML);
            this.javaMailSender.send(mimeMessage);

        } catch (MessagingException | MailException e) {
            System.out.println("Error send email :" + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMailFromTemplateSync(String to, String subject, String templateName,String username,Object value) {
        // Context co 1 cai locale , giup truyen ngon ngu vao ben trong
        Context context = new Context();
        //mame luc cron job
        context.setVariable("namee",username);

        context.setVariable("jobs",value);
        // name nay luc register
        context.setVariable("name",value);
        String content = templateEngine.process(templateName,context);
        this.sendMailAsync(to,subject,content,false,true);

    }


}
