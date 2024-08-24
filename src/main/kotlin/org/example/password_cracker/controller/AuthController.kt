package org.example.password_cracker.controller

import org.example.password_cracker.dto.registerDTO
import org.example.password_cracker.service.EmailService
import org.example.password_cracker.service.TokenService
import org.example.password_cracker.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AuthController(val userService: UserService, val emailService: EmailService, val tokenService: TokenService) {
    @GetMapping("/")
    fun root(): String = "redirect:/login"

    @GetMapping("/login")
    fun index(): String = "login"

    @GetMapping("/register")
    fun register(model: Model): String {
        if(!model.containsAttribute("password")) {
            model.addAttribute("password", false)
        } else if(!model.containsAttribute("email")) {
            model.addAttribute("email", false)
        }
        model.addAttribute("dto", registerDTO())
        return "register"
    }

    @PostMapping("/register")
    fun registerPost(@ModelAttribute dto: registerDTO, rda:RedirectAttributes): String {
        rda.addFlashAttribute("dto", dto)

        if(dto.password != dto.confirmPassword) {
            rda.addFlashAttribute("password", true)
            return "redirect:/register"
        } else if (userService.findUserByEmail(dto.email!!).isPresent) {
            rda.addFlashAttribute("email", true)
            return "redirect:/register?email=true"
        } else {
            val token = userService.register(dto)
            emailService.sendVerificationEmail(dto.email!!, token!!)
            return "redirect:/login?register=true"
        }
    }

    @GetMapping("/verify/{tokenId}")
    fun verify(@PathVariable tokenId: String): String {
        val token = tokenService.findToken(tokenId)

        if(token.isEmpty) {
            return "redirect:/login?notFound=true"
        } else if(token.get().isExpired()) {
            tokenService.deleteToken(tokenId)
            userService.deleteUser(token.get().user!!)
            return "redirect:/login?expired=true"
        } else {
            val user = token.get().user
            user!!.enabled = true
            userService.save(user)
            tokenService.deleteToken(tokenId)
            return "redirect:/login?verified=true"
        }
    }
}