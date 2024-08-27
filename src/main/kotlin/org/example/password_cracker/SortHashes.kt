package org.example.password_cracker

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File


class SortHashes: CommandLineRunner {

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


    override fun run(vararg args: String?) {
        println("Sorting hashes")
        val hashes = File("hashes.txt").readLines()
        val sortedHashes = mergeSort(hashes)

        File("sorted_hashes.txt").writeText(sortedHashes.joinToString("\n"))
        println("Hashes have been sorted")
    }

}