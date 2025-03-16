package vn.xuanhung.ELearning_Service.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import vn.xuanhung.ELearning_Service.constant.AppConstant;
import vn.xuanhung.ELearning_Service.dto.request.MailContentRequest;
import vn.xuanhung.ELearning_Service.dto.request.MailRequest;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {
    JavaMailSender mailSender;

    public String formGetActiveAccount(MailContentRequest mailRequest){
        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        .header {
                            background-color: #007bff;
                            color: white;
                            padding: 10px;
                            text-align: center;
                        }
                        .content {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            padding: 20px;
                        }
                        .footer {
                            font-size: 12px;
                            color: #777;
                            text-align: center;
                            padding: 10px;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="header">
                            <h1>%s</h1>
                        </div>
                        <div class="content">
                            <p>Hi, %s</p>
                            <p>Thank you for registering on our platform.</p>
                            <p>We are excited to have you onboard. Feel free to explore and reach out if you need any help.</p>
                            <a href="http://localhost:8080/elearning-service/accounts/active/%s">Please click here to activate your account.</a>
                        </div>
                        <div class="footer">
                            <p>&copy; 2025 Your Company. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(mailRequest.getTitle(), mailRequest.getTo(), mailRequest.getUserId());
        return html;
    }

    @KafkaListener(topics = AppConstant.Topic.EMAIL_TOPIC, groupId = "gr-sync-order", containerFactory = "kafkaListenerContainerFactory")
    public void sendHtmlEmail(ConsumerRecord<String, MailRequest> consumerRecord, Acknowledgment acknowledgment) {
        log.info("*Log mail service - sen mail verify account*");
        MailRequest mailRequest = consumerRecord.value();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(mailRequest.getToEmail());
            helper.setSubject(mailRequest.getSubject());
            helper.setText(mailRequest.getHtmlContent(), true); // Set true for HTML content
            helper.setFrom("no_reply@gmail.com");

            mailSender.send(message);
            acknowledgment.acknowledge();
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
