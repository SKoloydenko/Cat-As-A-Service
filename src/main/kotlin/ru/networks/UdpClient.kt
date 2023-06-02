package ru.networks

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*

class UdpClient {

    fun run(args: Array<String>) {
        runBlocking {
            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).udp().connect(InetSocketAddress("127.0.0.1", 9002))

            socket.openReadChannel()
            val sendChannel = socket.openWriteChannel(autoFlush = true)

            while (true) {
                try {
                    print("Input ID: ")
                    val id = readln()
                    print("Input food: ")
                    val food = readln()
                    sendChannel.writeStringUtf8("@$id - $food~")

                    val datagram = socket.receive()
                    println(datagram.packet.readText())
                } catch (e: Exception) {
                    println("Server closed a connection")
                    socket.close()
                    selectorManager.close()
                }
            }
        }
    }

}

fun main(args: Array<String>) {
    val client = UdpClient()
    client.run(args)
}