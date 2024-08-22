package org.example.password_cracker.controller

import lombok.RequiredArgsConstructor
import org.example.password_cracker.service.HomeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class HomeController(@Autowired private val homeService: HomeService) {

    @GetMapping("/")
    fun root(): String = "redirect:/login"

    @GetMapping("/login")
    fun index(): String = "login"

    @GetMapping("/home")
    fun home(@RequestParam("result", required = false) result: String?, model: Model): String {
        if(result != null) {
            model.addAttribute("result", result)
        }
        return "home"
    }

    @PostMapping("/home")
    fun homePost(@RequestParam("search") input: String, rda: RedirectAttributes): String {
        val result = homeService.saveEntryToFile(input)
        rda.addAttribute("result", result)
        return "redirect:/home"
    }


    @GetMapping("/crack")
    fun crack(): String = "crack"

    @GetMapping("/logout")
    fun logout(): String = "redirect:/login"

}