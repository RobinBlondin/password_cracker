package org.example.password_cracker.service

import org.example.password_cracker.dto.RegisterDTO
import org.example.password_cracker.model.Token
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Service
class AuthService(val tokenService: TokenService, val userService: UserService, val emailService: EmailService) {
    fun loadRegisterPageModelAttributes(model: Model): String {
        if(!model.containsAttribute("password")) {
            model.addAttribute("password", false)
        } else if(!model.containsAttribute("email")) {
            model.addAttribute("email", false)
        }
        model.addAttribute("dto", RegisterDTO())
        return "register"
    }

    fun registerProcess(rda: RedirectAttributes, dto: RegisterDTO): String {
        rda.addFlashAttribute("dto", dto)

        if(dto.password != dto.confirmPassword) {
            rda.addFlashAttribute("password", true)
            return "redirect:/register"
        } else if (userService.findUserByEmail(dto.email!!).isPresent) {
            rda.addFlashAttribute("email", true)
            return "redirect:/register?email=true"
        } else {
            val token = register(dto)
            emailService.sendVerificationEmail(dto.email!!, token!!)
            return "redirect:/login?register=true"
        }
    }

    fun register(dto: RegisterDTO): String? {
        val user = userService.dtoTOUser(dto)
        val token = Token(user = user)

        userService.save(user)
        tokenService.save(token)
        return token.token
    }

    fun verificationProcess(tokenId: String): String {
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