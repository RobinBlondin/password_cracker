package org.example.password_cracker

import org.springframework.boot.CommandLineRunner
import java.io.BufferedReader
import java.io.File
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class SortHashes : CommandLineRunner {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private fun merge(left: List<String>, right: List<String>): List<String> {
        var leftIndex = 0
        var rightIndex = 0
        val sortedList = mutableListOf<String>()

        while (leftIndex < left.size && rightIndex < right.size) {
            val (leftHash, rightHash) = left[leftIndex].split(" : ")[1] to right[rightIndex].split(" : ")[1]
            if (leftHash < rightHash) {
                sortedList.add(left[leftIndex])
                leftIndex++
            } else {
                sortedList.add(right[rightIndex])
                rightIndex++
            }
        }

        while (leftIndex < left.size) {
            sortedList.add(left[leftIndex])
            leftIndex++
        }

        while (rightIndex < right.size) {
            sortedList.add(right[rightIndex])
            rightIndex++
        }

        return sortedList
    }


    private fun mergeSort(list: List<String>): List<String> {
        if (list.size <= 1) {
            return list
        }

        val middle = list.size / 2
        var left = list.subList(0, middle)
        var right = list.subList(middle, list.size)

        left = mergeSort(left)
        right = mergeSort(right)

        return merge(left, right)
    }

    private fun splitFileIntoSortedChunks(inputFile: String, chunkSize: Int) {
        val list = mutableListOf<String>()
        var count = 0
        var totalLinesRead = 0
        val startTime = LocalTime.now()
        println("Splitting file at ${formatter.format(startTime)}")
        File(inputFile).useLines { allLines ->
            allLines.forEach { line ->
                list.add(line)
                totalLinesRead++
                if (list.size == chunkSize) {
                    sortAndWriteListToFile("files/sorted_chunks/chunk$count.txt", list)
                    countAndPrintDurationOfChunkSplit(startTime, count)
                    count++
                    list.clear()
                }
            }
        }

        if (list.isNotEmpty()) {
            sortAndWriteListToFile("files/sorted_chunks/chunk$count.txt", list)
            countAndPrintDurationOfChunkSplit(startTime, count)
        }
        val end = LocalTime.now()

        println("File splitting completed. Total lines read: $totalLinesRead")
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
        deleteChunks(inputFiles)
        val end = LocalTime.now()
        println("Merging completed after ${Duration.between(start, end).toMinutes()} minutes")
    }

    private fun countAndPrintDurationOfChunkSplit(startTime: LocalTime, chunkNumber: Int) {
        val chunkTime = LocalTime.now()
        val duration = Duration.between(startTime, chunkTime)
        println("Chunk $chunkNumber finished after ${duration.toMinutes()} min - ${duration.toSeconds() % 60}s")
    }

    private fun deleteChunks(files: List<String>) {
        files.forEach { fileName ->
            val file = File(fileName)
            if (file.exists()) {
                file.delete()
            }
        }
    }


    private fun sortAndWriteListToFile(fileName: String, list: List<String>) {
        val sortedList = mergeSort(list)
        File(fileName).bufferedWriter().use { writer ->
            val listToString = sortedList.joinToString("\n")
            writer.write(listToString)
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
            splitFileIntoSortedChunks("files/hashes.txt", 4000000)
            val inputFiles = listChunkPathsInFolder("files/sorted_chunks/")
            mergeSortedChunks(inputFiles)
        } catch (e: Exception) {
            print(e.message)
            e.printStackTrace()
        }
    }

}