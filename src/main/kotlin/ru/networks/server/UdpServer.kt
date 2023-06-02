package ru.networks.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import ru.networks.storage.UserFeedCountStorage
import java.util.*

class UdpServer {

    private val userFeedCountStorage: UserFeedCountStorage = UserFeedCountStorage()

    suspend fun run(port: Int) {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val socketAddress = InetSocketAddress("127.0.0.1", port)
        val socket = aSocket(selectorManager).udp().bind(socketAddress)
        socket.openReadChannel()

        println("UDP server is listening on port $port")

        try {
            while (true) {
                socket.listen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            socket.close()
            selectorManager.close()
        }
    }

    private suspend fun BoundDatagramSocket.listen() {
        val datagram = receive()
        val data = datagram.packet.readText()
        val responsePacket = if (Regex("@.+ - .+~").matches(data)) {
            handleData(data)
        } else {
            buildPacket { writeText("Invalid input") }
        }
        send(Datagram(responsePacket, datagram.address))
    }

    private fun handleData(data: String): ByteReadPacket {
        val (id, food) = data.removePrefix("@").removeSuffix("~").split(" - ")

        val response =  if (isValidFood(food)) {
            userFeedCountStorage.updateFeedCount(id)
            println("User $id fed cat")
            "Eaten by the Cat"
        } else {
            "Ignored by the Cat"
        }

        return buildPacket { writeText(response) }
    }

    private fun isValidFood(food: String): Boolean {
        val validFoods = listOf("fish", "meat", "milk", "bread", "carrot", "beer")
        return validFoods.contains(food.lowercase(Locale.getDefault()))
    }
}