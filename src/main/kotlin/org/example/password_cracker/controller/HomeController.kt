package org.example.password_cracker.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun root(): String = "redirect:/home"

    @GetMapping("/login")
    fun index(): String = "login"

    @GetMapping("/home")
    fun home(): String = "home"

    @PostMapping("/home")
    fun homePost(): String = "redirect:/home"
}