package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class HashPasswords: CommandLineRunner{
    private val homeService = HomeService()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    override fun run(vararg args: String?) {
        val startTime = LocalTime.now()
        var linesRead = 0
        println("Hashing of passwords started at ${startTime.format(formatter)}")
        File("files/passwords.txt").useLines { lines ->
            File("files/hashes.txt").printWriter().use { writer ->
                lines.forEach {
                    if(linesRead % 1000000 == 0){
                        val interval = LocalTime.now()
                        println("$linesRead passwords hashed after " +
                                "${Duration.between(startTime, interval).toHoursPart()} hours and " +
                                "${Duration.between(startTime, interval).toMinutesPart()} minutes and " +
                                "${Duration.between(startTime, interval).toSecondsPart()} seconds")
                    }
                    val hash = homeService.encode(it, "SHA-256")
                    writer.println("$it : $hash")
                    linesRead++
                }
            }

        }
        println("File has been parsed")
    }
}