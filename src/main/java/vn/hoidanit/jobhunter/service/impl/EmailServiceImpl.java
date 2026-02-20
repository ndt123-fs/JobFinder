package vn.hoidanit.jobhunter.service.impl;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    private final MailSender mailSender;
    public EmailServiceImpl(MailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleEmail(){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("2351050180tin@ou.edu.vn"," 2351050020duy@gmail.com","nguyenduytintin028@gmail.com","2351050178tien@gmail.com");
        msg.setSubject("Deadline spring boot !");
        msg.setText("My name is SUPER_TIN , dcm bay lo lam bai tap lon di nghe chua");
        this.mailSender.send(msg);

    }

}
