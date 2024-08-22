package org.example.password_cracker.enums

enum class Role(private val roleName: String) {
    ADMIN("ADMIN"),
    CLIENT("CLIENT");

    override fun toString(): String {
        return roleName
    }
}