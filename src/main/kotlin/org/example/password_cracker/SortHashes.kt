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

    private fun splitFileIntoSortedChunks(inputFile: String, chunkSize: Int) {
        val chunkSizeInBytes = chunkSize * 1024 * 1024L
        val startTime = LocalTime.now()
        println("Splitting file at ${formatter.format(startTime)}")

        RandomAccessFile(inputFile, "r").use { file ->
            var chunkNumber = 1
            val buffer = ByteArray(chunkSizeInBytes.toInt())

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

                val outputFileName = "files/sorted_chunks/chunk_$chunkNumber.txt"

                val sortedString = String(buffer.copyOfRange(0, adjustedBytesRead))
                    .trim()
                    .split("\n")
                    .sortedBy { it.substringAfter(" : ") }
                    .joinToString("\n")

                countAndPrintDurationOfChunkSplit(startTime, chunkNumber)

                File(outputFileName).writeText(sortedString)

                file.seek(file.filePointer - (bytesRead - adjustedBytesRead))

                chunkNumber++
            }
        }
        val end = LocalTime.now()
        println("Completed after ${Duration.between(startTime, end).toMinutes()} minutes")
    }


    private fun mergeSortedChunks(inputFiles: List<String>) {
        val start = LocalTime.now()
        println("Merging files at ${formatter.format(start)}")
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
        deleteChunksAfterMerge(inputFiles)

        val end = LocalTime.now()
        println("Merging completed after ${Duration.between(start, end).toMinutes()} minutes")
    }

    private fun countAndPrintDurationOfChunkSplit(startTime: LocalTime, chunkNumber: Int) {
        val chunkTime = LocalTime.now()
        val duration = Duration.between(startTime, chunkTime)
        println("Chunk $chunkNumber finished after ${duration.toMinutes()} min - ${duration.toSeconds() % 60}s")
    }

    private fun deleteChunksAfterMerge(files: List<String>) {
        files.forEach { fileName ->
            val file = File(fileName)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun listChunkPathsInFolder(folderPath: String): List<String> {
        val folder = File(folderPath)
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Folder $folderPath do not exist")
        }

        return folder.listFiles()?.filter { it.isFile }?.map { it.absolutePath } ?: emptyList()
    }


    override fun run(vararg args: String?) {
        try {
            splitFileIntoSortedChunks("files/hashes.txt", 300)
            val inputFiles = listChunkPathsInFolder("files/sorted_chunks/")
            mergeSortedChunks(inputFiles)
        } catch (e: Exception) {
            print(e.message)
            e.printStackTrace()
        }
    }

}