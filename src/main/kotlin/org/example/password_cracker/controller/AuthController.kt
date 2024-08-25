package org.example.password_cracker.controller

import jakarta.servlet.http.HttpServletRequest
import org.example.password_cracker.dto.RegisterDTO
import org.example.password_cracker.service.AuthService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AuthController(val authService: AuthService) {
    @GetMapping("/")
    fun root(): String = "redirect:/login"

    @GetMapping("/login")
    fun index(request: HttpServletRequest, model: Model): String {
        val error = request.getParameter("error")
        if(error != null) {
            model.addAttribute("error_message", error)
        }
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String = authService.loadRegisterPageModelAttributes(model)


    @PostMapping("/register")
    fun registerPost(@ModelAttribute dto: RegisterDTO, rda:RedirectAttributes): String = authService.registerProcess(rda, dto)


    @GetMapping("/verify/{tokenId}")
    fun verify(@PathVariable tokenId: String): String = authService.verificationProcess(tokenId)

}