package vn.hoidanit.jobhunter.service;

public interface EmailService {
    void sendSimpleEmail();

    void sendMailAsync(String to, String subject, String content, boolean isMultipart, boolean isHTML);

    void sendMailFromTemplateSync(String to, String subject, String templateName,String username,Object value);
}
