package org.example.password_cracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.example.password_cracker.service.HomeService
import org.springframework.boot.CommandLineRunner
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HashPasswords : CommandLineRunner {
    private val homeService = HomeService()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val bufferSize = 100000
    private val totalBytes = File("files/passwords.txt").length()
    private var amountOfBytes = 0L
    private var lastLoggedProgress = 0L

    override fun run(vararg args: String?) = runBlocking {
        val startTime = LocalDateTime.now()
        var linesRead = 0
        val buffer = StringBuilder()
        log("Hashing of passwords started ----> ${startTime.format(formatter)}")

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
                        amountOfBytes += line.substringBefore(" : ").toByteArray().size
                    }

                    val progress = (amountOfBytes * 100 / totalBytes)

                    if (progress > lastLoggedProgress || progress == 100L) {
                        log("Hashing progress: ${progress}%", startTime, true)
                        lastLoggedProgress = progress
                    }

                    writer.write(buffer.toString())
                    buffer.clear()
                }
            }
        }

        log("Hashing of passwords completed. Total passwords hashed: $linesRead")
    }

    private fun log(message: String, startTime: LocalDateTime = LocalDateTime.now(), overwrite: Boolean = false) {
        if (overwrite) {
            print("\r> $message ----> ${durationLog(startTime)}")
        } else {
            println("> $message")
        }
    }

    private fun durationLog(startTime: LocalDateTime): String {
        val duration = Duration.between(startTime, LocalDateTime.now())
        return "Time taken: ${duration.toHoursPart()} hours " +
                "${duration.toMinutesPart()} minutes and " +
                "${duration.toSecondsPart()} seconds"

    }
}