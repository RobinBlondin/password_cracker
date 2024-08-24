package org.example.password_cracker.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import java.time.LocalDateTime
import java.util.*

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Token(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var token: String? = UUID.randomUUID().toString(),
    private var expiryDate: LocalDateTime = LocalDateTime.now().plusMinutes(1),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    var user: User? = null
) {

    fun isExpired(): Boolean = LocalDateTime.now().isAfter(expiryDate)
}