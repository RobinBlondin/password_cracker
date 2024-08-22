package org.example.password_cracker.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import org.example.password_cracker.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private var id: UUID? = null,
    private var email: String? = null,
    private var name: String? = null,
    private var password: String? = null,
    private var enabled: Boolean = false,
    @Enumerated(EnumType.STRING)
    private var role: Role = Role.CLIENT
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
        mutableListOf(SimpleGrantedAuthority(role.toString()))

    override fun getPassword(): String = password ?: ""

    override fun getUsername(): String = email ?: ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled
}