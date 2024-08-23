package org.example.password_cracker.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class AuthController {
    @GetMapping("/")
    fun root(): String = "redirect:/login"

    @GetMapping("/login")
    fun index(): String = "login"

    @GetMapping("/register")
    fun register(): String = "register"

    @PostMapping("/register")
    fun registerPost(): String = "redirect:/login"
}