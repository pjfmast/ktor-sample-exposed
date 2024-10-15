package avans.avd

import avans.avd.model.DatabaseRepository
import avans.avd.model.FakeTaskRepository
import avans.avd.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    val repository = FakeTaskRepository()
    val repository = DatabaseRepository()

    configureSerialization(repository)
    configureDatabases()
    configureRouting()
}
