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
    private val totalBytes = File("files/passwords.txt").length() * 2
    private var amountOfBytes = 0L
    private var lastLoggedProgress = 0L


    override fun run(vararg args: String?) = runBlocking {
        val startTime = LocalDateTime.now()
        var linesRead = 0
        log("Hashing of passwords started ----> ${startTime.format(formatter)}")

        BufferedWriter(FileWriter("files/hashes_md5.txt")).use { writerMd5 ->
            BufferedWriter(FileWriter("files/hashes_sha256.txt")).use { writerSha256 ->
                File("files/passwords.txt").useLines { lines ->
                    val chunkedLines = lines.chunked(bufferSize)

                    chunkedLines.forEach { chunk ->
                        val deferredResults = chunk.map { password ->
                            async(Dispatchers.Default) {
                                val hashSha256 = homeService.encode(password, "SHA-256")
                                val hashMd5 = homeService.encode(password, "MD5")

                                Pair("$password : $hashMd5", "$password : $hashSha256")
                            }
                        }

                        val result = deferredResults.awaitAll()
                        writerMd5.apply {
                            result.forEach { (md5, _) -> write("$md5\n") }
                        }
                        writerSha256.apply {
                            result.forEach { (_, sha256) -> write("$sha256\n") }
                        }

                        linesRead += chunk.size
                        result.forEach { (md5, sha256) -> amountOfBytes += lineIntoBytes(md5) + lineIntoBytes(sha256) }

                        val progress = amountOfBytes * 100 / totalBytes
                        if (progress > lastLoggedProgress) {
                            log("Hashing progress: ${progress}%", startTime, true)
                            lastLoggedProgress = progress
                        }
                    }
                }
            }
        }
        //File("files/passwords.txt").delete()
        println()
        log("Hashing of passwords completed. Total passwords hashed: $linesRead")
    }

    private fun lineIntoBytes(line: String): Int {
        return line.substringBefore(" : ").toByteArray().size + "\n".toByteArray().size
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