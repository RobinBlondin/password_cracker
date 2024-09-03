package org.example.password_cracker.controller

import jakarta.servlet.http.HttpServletRequest
import org.example.password_cracker.dto.RegisterDTO
import org.example.password_cracker.service.AuthService
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
class AuthController(
    val authService: AuthService,
    val userService: UserService,
    val tokenService: TokenService
) {
    @GetMapping("/")
    fun root(): String = "redirect:/login"

    @GetMapping("/login")
    fun index(request: HttpServletRequest, model: Model): String {
        val error = request.getParameter("error")
        if(error != null) {
            model.addAttribute("error_message", error)
        }

        if(!model.containsAttribute("message")) {
            model.addAttribute("message", "")
        }

        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
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


    @PostMapping("/register")
    fun registerPost(@ModelAttribute dto: RegisterDTO, rda:RedirectAttributes): String {
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

        authService.registerProcess(rda, dto)

        rda.addFlashAttribute("message", "Verification email sent")
        return "redirect:/login"
    }



    @GetMapping("/verify/{tokenId}")
    fun verify(@PathVariable tokenId: String, rda: RedirectAttributes): String {
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

        authService.verificationProcess(tokenId, existingToken)

        rda.addFlashAttribute("message", "Account has been verified. Please login.")
        return "redirect:/login"
    }

}