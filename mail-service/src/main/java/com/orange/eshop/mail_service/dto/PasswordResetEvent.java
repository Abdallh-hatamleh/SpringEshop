package com.orange.eshop.mail_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEvent implements Serializable {

    private String email;
    private String resetToken;
}
