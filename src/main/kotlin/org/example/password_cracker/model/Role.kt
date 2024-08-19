package org.example.password_cracker.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
    val name: String? = null
}