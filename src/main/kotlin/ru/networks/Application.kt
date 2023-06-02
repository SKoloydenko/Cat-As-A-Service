package ru.networks

import kotlinx.coroutines.*
import ru.networks.server.TcpServer
import ru.networks.server.UdpServer
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

class Application {

    init {
        val dataDirPath = Path("data")
        if (dataDirPath.notExists()) {
            dataDirPath.createDirectory()
        }
    }

    fun run() {
        val udpPort = 9002
        val tcpPort = 9003

        val udpServer = UdpServer()
        val tcpServer = TcpServer()

        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                udpServer.run(udpPort)
            }

            CoroutineScope(Dispatchers.IO).launch {
                tcpServer.run(tcpPort)
            }

            delay(Long.MAX_VALUE)
        }
    }

}

fun main() {
    val app = Application()
    app.run()
}
