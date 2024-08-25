package org.example.password_cracker.service

import org.example.password_cracker.dto.RegisterDTO
import org.example.password_cracker.model.Token
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Service
class AuthService(val tokenService: TokenService, val userService: UserService, val emailService: EmailService) {
    fun loadRegisterPageModelAttributes(model: Model): String {
        if(!model.containsAttribute("dto")) {
            model.addAttribute("dto", RegisterDTO())
        }

        if (!model.containsAttribute("password")) {
            model.addAttribute("password", false)
        }

        if (!model.containsAttribute("email")) {
            model.addAttribute("email", false)
        }

        return "register"
    }

    fun registerProcess(rda: RedirectAttributes, dto: RegisterDTO): String {
        if (dto.password != dto.confirmPassword) {
            rda.addFlashAttribute("message", "Passwords do not match")
            rda.addFlashAttribute("dto", dto)
            return "redirect:/register"
        }

        if (userService.findUserByEmail(dto.email!!).isPresent) {
            rda.addFlashAttribute("message", "Email already in use")
            rda.addFlashAttribute("dto", dto)
            return "redirect:/register"
        }

        val token = createUserAndToken(dto)
        val email = dto.email ?: return "redirect:/register?error=true"
        emailService.sendVerificationEmail(email, token)
        rda.addFlashAttribute("message", "Verification email sent")
        return "redirect:/login"
    }

    fun createUserAndToken(dto: RegisterDTO): String {
        val user = userService.dtoTOUser(dto)
        val token = Token(user = user)

        userService.save(user)
        tokenService.save(token)

        val tokenName = token.token ?: return "redirect:/register?error=true"
        return tokenName
    }

    fun verificationProcess(tokenId: String, rda: RedirectAttributes): String {
        val token = tokenService.findToken(tokenId)

        if (token.isEmpty) {
            rda.addFlashAttribute("message", "Verification token not found")
            return "redirect:/login"
        }

        val existingToken = token.get()
        if (existingToken.isExpired()) {
            tokenService.deleteToken(tokenId)
            userService.deleteUser(token.get().user!!)
            rda.addFlashAttribute("message", "Verification token expired. Please register again.")
            return "redirect:/login"
        }

        val user = existingToken.user ?: return "redirect:/login?notFound=true"
        user.enabled = true
        userService.save(user)
        tokenService.deleteToken(tokenId)
        rda.addFlashAttribute("message", "Account has been verified. Please login.")
        return "redirect:/login"

    }
}