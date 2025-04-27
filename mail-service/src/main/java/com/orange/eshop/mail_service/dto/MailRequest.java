package com.orange.eshop.mail_service.dto;

import lombok.Data;

@Data
public class MailRequest {
    private String to;
    private String subject;
    private String body;
}
