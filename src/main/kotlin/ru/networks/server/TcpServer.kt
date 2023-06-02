package ru.networks.server

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import ru.networks.storage.UserFeedCountStorage
import ru.networks.storage.UserPetStorage

class TcpServer {

    private val userFeedCountStorage: UserFeedCountStorage = UserFeedCountStorage()
    private val userPetStorage: UserPetStorage = UserPetStorage()

    private val petCountLimit = 3

    suspend fun run(port: Int) {
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val socketAddress = InetSocketAddress("127.0.0.1", port)
        val server = aSocket(selectorManager).tcp().bind(socketAddress)

        println("TCP server is listening on port $port")

        try {
            while (true) {
                server.listen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            server.close()
            selectorManager.close()
        }
    }

    private suspend fun ServerSocket.listen() {
        val socket = accept()

        CoroutineScope(Dispatchers.IO).launch {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            while (true) {
                val data = input.readUTF8Line()
                if (data == null || !Regex("@.+~").matches(data)) {
                    output.writeStringUtf8("Invalid input\n")
                    socket.close()
                    break
                }

                val id = data.removePrefix("@").removeSuffix("~")
                if (userFeedCountStorage.containsById(id)) {
                    val count = userPetStorage.getPetCount(id)
                    if (count == petCountLimit) {
                        output.writeStringUtf8("Scratched by the Cat\n")
                        userPetStorage.resetPetCount(id)
                        socket.close()
                        break
                    }
                    userPetStorage.updatePetCount(id)
                    output.writeStringUtf8("Tolerated by the Cat\n")
                } else {
                    output.writeStringUtf8("Scratched by the Cat\n")
                }
            }
        }
    }
}
