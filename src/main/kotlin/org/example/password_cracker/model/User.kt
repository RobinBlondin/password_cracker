package org.example.password_cracker.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import java.util.UUID

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity

class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
    val email: String? = null
    val name: String? = null
    val password: String? = null

}