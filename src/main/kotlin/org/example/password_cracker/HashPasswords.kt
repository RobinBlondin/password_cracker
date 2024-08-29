package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class HashPasswords : CommandLineRunner {
    private val homeService = HomeService()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val chunkSize = 100000

    override fun run(vararg args: String?) {
        val startTime = LocalTime.now()
        var linesRead = 0
        val buffer = StringBuilder()
        println("Hashing of passwords started at ${startTime.format(formatter)}")

        File("files/passwords.txt").useLines { lines ->
            BufferedWriter(FileWriter("files/hashes.txt")).use { writer ->
                lines.forEach {
                    if (linesRead > 0 && linesRead % 1000000 == 0) {
                        val interval = LocalTime.now()
                        val duration = Duration.between(startTime, interval)
                        println("Hashed $linesRead passwords after " +
                                "${duration.toHoursPart()} hours " +
                                "${duration.toMinutesPart()} minutes and " +
                                "${duration.toSecondsPart()} seconds"
                        )
                    }
                    val hash = homeService.encode(it, "SHA-256")
                    buffer.append("$it : $hash\n")
                    linesRead++

                    if (linesRead % chunkSize == 0) {
                        writer.write(buffer.toString())
                        buffer.clear()
                    }
                }
                if (buffer.isNotEmpty()) {
                    writer.write(buffer.toString())
                }
            }
        }

        val duration = Duration.between(startTime, LocalTime.now())
        println("Hashing of passwords completed after " +
                "${duration.toHoursPart()} hours " +
                "${duration.toMinutesPart()} minutes and " +
                "${duration.toSecondsPart()} seconds" +
                " with a total of $linesRead passwords hashed"
        )
    }
}