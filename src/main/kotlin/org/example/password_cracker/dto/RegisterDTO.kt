package org.example.password_cracker.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
class RegisterDTO {
    var name: String? = null
    var email: String? = null
    var password: String? = null
    var confirmPassword: String? = null
}