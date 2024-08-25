package org.example.password_cracker.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import org.example.password_cracker.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var email: String? = null,
    var name: String? = null,
    private var password: String? = null,
    var enabled: Boolean = false,
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