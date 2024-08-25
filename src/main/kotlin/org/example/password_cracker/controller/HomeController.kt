package org.example.password_cracker.controller

import org.example.password_cracker.service.HomeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class HomeController(@Autowired private val homeService: HomeService) {

    @GetMapping("/home")
    fun home(): String = "home"

    @PostMapping("/home")
    fun homePost(@RequestParam("search") input: String, rda: RedirectAttributes): String {
        if(input.isNotEmpty()) {
            val result = homeService.saveEntryToFile(input)
            rda.addFlashAttribute("sha256", result.first)
            rda.addFlashAttribute("md5", result.second)
        }

        return "redirect:/home"
    }

    @GetMapping("/crack")
    fun crack(): String = "crack"

    @PostMapping("/crack")
    fun crackPost(@RequestParam("search") hash: String, rda: RedirectAttributes): String {
        val result = homeService.crackHash(hash)
        rda.addFlashAttribute("result", result)
        return "redirect:/crack"
    }

    @GetMapping("/logout")
    fun logout(): String = "redirect:/login"

}