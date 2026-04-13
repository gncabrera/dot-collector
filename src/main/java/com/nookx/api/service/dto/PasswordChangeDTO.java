package com.nookx.api.service.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A DTO representing a password change required data - current and new password.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String currentPassword;
    private String newPassword;
}
