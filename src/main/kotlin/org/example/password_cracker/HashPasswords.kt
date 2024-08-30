package org.example.password_cracker

import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import kotlinx.coroutines.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class HashPasswords : CommandLineRunner {
    private val homeService = HomeService()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val bufferSize = 100000

    override fun run(vararg args: String?) = runBlocking {
        val startTime = LocalTime.now()
        var linesRead = 0
        val buffer = StringBuilder()
        log("Hashing of passwords started -> ${startTime.format(formatter)}", withDuration = false)

        BufferedWriter(FileWriter("files/hashes.txt")).use { writer ->
            File("files/passwords.txt").useLines { lines ->
                val chunkedLines = lines.chunked(bufferSize)

                chunkedLines.forEach { chunk ->
                    val postponedResults = chunk.map { password ->
                      async (Dispatchers.Default) {
                            val hash = homeService.encode(password, "SHA-256")
                            "$password : $hash"
                        }
                    }

                    val result = postponedResults.awaitAll()

                    result.forEach { line ->
                        buffer.append("$line\n")
                        linesRead++
                    }

                    if (linesRead > 0 && linesRead % 1000000 == 0) {
                        log("Hashed $linesRead passwords. ", startTime)
                    }

                    writer.write(buffer.toString())
                    buffer.clear()
                }
            }
        }

        log("Hashing of passwords completed. Total passwords hashed: $linesRead", startTime)
    }

    private fun log(message: String, startTime: LocalTime = LocalTime.now(), withDuration: Boolean = true) {
        println(message)
        if (withDuration) {
            val duration = Duration.between(startTime, LocalTime.now())
            println(
                "Time taken: ${duration.toHoursPart()} hours " +
                        "${duration.toMinutesPart()} minutes and " +
                        "${duration.toSecondsPart()} seconds\n"
            )
        }

    }
}