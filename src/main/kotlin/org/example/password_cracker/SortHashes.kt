package org.example.password_cracker

import org.springframework.boot.CommandLineRunner
import java.io.BufferedReader
import java.io.File
import java.io.RandomAccessFile
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class SortHashes : CommandLineRunner {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private fun partitionAndSortFile() {

        val startTime = LocalTime.now()
        log("File splitting started -> ${formatter.format(startTime)}", startTime, false)

        RandomAccessFile("files/hashes.txt", "r").use { file ->
            val fileSize = file.length()
            val partitionSizeInBytes = setPartitionSize(fileSize)
            var partitionNo = 1
            val totalPartitions = (fileSize / partitionSizeInBytes).coerceAtLeast(1)

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

                val outputFileName = "files/partitions/partition_$partitionNo.txt"

                val sortedString = String(buffer.copyOfRange(0, adjustedBytesRead))
                    .trim()
                    .split("\n")
                    .sortedBy { it.substringAfter(" : ") }
                    .joinToString("\n")

                log("Part $partitionNo / $totalPartitions completed", startTime)

                File(outputFileName).writeText(sortedString)

                file.seek(file.filePointer - (bytesRead - adjustedBytesRead))
                partitionNo++
            }
        }
        log("File splitting completed.", startTime)
    }


    private fun setPartitionSize(fileSize: Long): Long {
        return when {
            fileSize <= 100 * 1024 * 1024L -> 10 * 1024 * 1024L
            fileSize <= 1 * 1024 * 1024 * 1024L -> 50 * 1024 * 1024L
            fileSize <= 10 * 1024 * 1024 * 1024L -> 100 * 1024 * 1024L
            else -> 300 * 1024 * 1024L
        }
    }


    private fun mergePartitions(inputFiles: List<String>) {
        val start = LocalTime.now()
        log("Merging of files started -> ${formatter.format(start)}", start,false)
        val outputFile = File("files/sorted_hashes.txt")

        val pq = PriorityQueue<Pair<String, BufferedReader>>(compareBy { it.first.split(" : ")[1] })
        val readers = inputFiles.map { File(it).bufferedReader() }

        readers.forEach { reader ->
            val line = reader.readLine()
            if (line != null) {
                pq.add(Pair(line, reader))
            }
        }


        outputFile.bufferedWriter().use { writer ->
            while (pq.isNotEmpty()) {
                val (line, reader) = pq.poll()

                writer.write(line)
                writer.newLine()

                val nextLine = reader.readLine()

                if (nextLine != null) {
                    pq.add(Pair(nextLine, reader))
                }
            }
        }
        readers.forEach { it.close() }
        deletePartitions(inputFiles)

        log("Merging completed", start)
    }


    private fun deletePartitions(files: List<String>) {
        files.forEach { fileName ->
            val file = File(fileName)
            if (file.exists()) {
                file.delete()
            }
        }
    }


    private fun listOfPartitionPaths(): List<String> {
        val folder = File("files/partitions/")
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Folder 'files/partitions/' do not exist")
        }

        return folder.listFiles()?.filter { it.isFile }?.map { it.absolutePath } ?: emptyList()
    }


    override fun run(vararg args: String?) {
        try {
            partitionAndSortFile()
            val inputFiles = listOfPartitionPaths()
            mergePartitions(inputFiles)
        } catch (e: Exception) {
            print(e.message)
            e.printStackTrace()
        }
    }

    private fun log(message: String, startTime: LocalTime, withDuration: Boolean = true) {
        println(message)
        if(!withDuration) {
            return
        }

        val duration = Duration.between(startTime, LocalTime.now())
        println("Time taken: ${duration.toHoursPart()} hours " +
                "${duration.toMinutesPart()} minutes and " +
                "${duration.toSecondsPart()} seconds\n"
        )
    }

}