package ru.networks

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {

    runBlocking {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(InetSocketAddress("127.0.0.1", 9003))

        val receiveChannel = socket.openReadChannel()
        val sendChannel = socket.openWriteChannel(autoFlush = true)

        launch(Dispatchers.IO) {
            try {
                while (true) {
                    print("Input ID: ")
                    val id = readln()
                    sendChannel.writeStringUtf8("@$id~\n")

                    val response = receiveChannel.readUTF8Line()
                    if (response == null) {
                        println("Server closed a connection")
                        socket.close()
                        break
                    }
                    println(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                socket.close()
            }
        }
    }

}