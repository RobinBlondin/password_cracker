package org.example.password_cracker

import org.springframework.boot.CommandLineRunner
import java.io.BufferedReader
import java.io.File
import java.io.RandomAccessFile
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SortHashes : CommandLineRunner {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val partitions = mutableListOf<File>()

    private fun partitionAndSortFile() {
        val startTime = LocalDateTime.now()
        log("File splitting started -> ${formatter.format(startTime)}")

        RandomAccessFile("files/hashes.txt", "r").use { file ->
            val fileSize = file.length()
            val partitionSizeInBytes = setPartitionSize(fileSize)
            var partitionNo = 1
            val totalPartitions = (fileSize / partitionSizeInBytes) + 1
            val buffer = ByteArray(partitionSizeInBytes.toInt())

            while (true) {
                val bytesRead = file.read(buffer)

                if (bytesRead == -1) {
                    break
                }

                var adjustedBytesRead = 0

                for (i in bytesRead - 1 downTo 0) {
                    if (buffer[i] == '\n'.code.toByte()) {
                        adjustedBytesRead = i + 1
                        break
                    }
                }

                val outputFile = File("files/partitions/partition_$partitionNo.txt")

                val result = String(buffer.copyOfRange(0, adjustedBytesRead))
                    .trim()
                    .split("\n")
                    .sortedBy { it.substringAfter(" : ") }
                    .joinToString("\n")

                outputFile.writeText(result)
                partitions.add(outputFile)
                log("Part $partitionNo / $totalPartitions completed", startTime, true)

                file.seek(file.filePointer - (bytesRead - adjustedBytesRead))
                partitionNo++
            }
        }
        File("files/hashes.txt").delete()
        println("> ")
        log("File splitting completed.")
        mergePartitions(partitions)
    }


    private fun setPartitionSize(fileSize: Long): Long {
        return when {
            fileSize <= 100 * 1024 * 1024L -> 10 * 1024 * 1024L
            fileSize <= 1 * 1024 * 1024 * 1024L -> 50 * 1024 * 1024L
            fileSize <= 10 * 1024 * 1024 * 1024L -> 100 * 1024 * 1024L
            else -> 300 * 1024 * 1024L
        }
    }


    private fun mergePartitions(inputFiles: List<File>) {
        val totalBytes = inputFiles.sumOf { it.length() }
        var amountOfBytes = 0L
        var lastLoggedProgress = 0L
        val start = LocalDateTime.now()
        val outputFile = File("files/sorted_hashes.txt")
        val pq = PriorityQueue<Pair<String, BufferedReader>>(compareBy { it.first.split(" : ")[1] })
        val readers = inputFiles.map { it.bufferedReader() }

        log("Merging of files started ----> ${formatter.format(start)}")

        fun deleteFile(reader: BufferedReader) {
            val index = readers.indexOf(reader)
            val file = inputFiles[index]
            file.delete()
        }

        readers.forEach { reader ->
            val line = reader.readLine()
            amountOfBytes += line.toByteArray().size

            if (line != null) {
                amountOfBytes += line.toByteArray().size
                pq.add(Pair(line, reader))
            } else {
                deleteFile(reader)
            }
        }

        outputFile.bufferedWriter().use { writer ->
            while (pq.isNotEmpty()) {
                val (line, reader) = pq.poll()

                writer.write(line)
                writer.newLine()

                amountOfBytes += lineIntoBytes(line)
                val progress = (amountOfBytes * 100 / totalBytes)

                if (progress > lastLoggedProgress || progress == 100L) {
                    log("Merging progress: ${progress}%", start, true)
                    lastLoggedProgress = progress
                }

                val nextLine = reader.readLine()
                if (nextLine != null) {
                    pq.add(Pair(nextLine, reader))
                } else {
                    deleteFile(reader)
                }
            }
        }
        readers.forEach { it.close() }

        println("> ")
        log("Merging completed")
    }

    private fun lineIntoBytes(line: String): Int {
        return line.toByteArray().size + "\n".toByteArray().size
    }

    override fun run(vararg args: String?) {
        try {
            partitionAndSortFile()
        } catch (e: Exception) {
            print(e.message)
            e.printStackTrace()
        }
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