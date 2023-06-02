package ru.networks.storage

import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.notExists

class UserPetStorage {

    private val path = Path("data/userPetCount.txt")
    private val file = path.toFile()

    fun updatePetCount(id: String) {
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

    fun resetPetCount(id: String) {
        val lines = file.readLines()
        val updatedLines = mutableListOf<String>()

        for (line in lines) {
            val (userId, _) = line.split(" ")
            if (userId != id) {
                updatedLines.add(line)
            }
        }

        file.writeText(updatedLines.joinToString("\n"))
    }

    fun getPetCount(id: String): Int {
        val lines = file.readLines()

        for (line in lines) {
            val (userId, count) = line.split(" ")
            if (userId == id) {
                return count.toInt()
            }
        }

        return 0
    }

    init {
        if (path.notExists()) {
            path.createFile()
        }
    }

}