package ru.networks.storage

import kotlin.io.path.*

class UserFeedCountStorage {

    private val path = Path("data/userFeedCount.txt")
    private val file = path.toFile()

    fun updateFeedCount(id: String) {
        val lines = file.readLines()
        val updatedLines = mutableListOf<String>()
        var userFound = false

        for (line in lines) {
            val (userId, count) = line.split(" ")
            if (userId == id) {
                updatedLines.add("$id ${count.toInt() + 1}")
                userFound = true
            } else {
                updatedLines.add(line)
            }
        }

        if (!userFound) {
            updatedLines.add("$id 1")
        }

        file.writeText(updatedLines.joinToString("\n"))
    }

    fun containsById(id: String): Boolean {
        val lines = file.readLines()
        var userFound = false

        for (line in lines) {
            val (userId, _) = line.split(" ")
            if (userId == id) {
                userFound = true
            }
        }

        return userFound
    }

    init {
        if (path.notExists()) {
            path.createFile()
        }
    }

}