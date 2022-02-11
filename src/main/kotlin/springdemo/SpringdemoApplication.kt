package springdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

@SpringBootApplication
class SpringdemoApplication

fun main(args: Array<String>) {
    runApplication<SpringdemoApplication>(*args)
}

@RestController
class MessageResource(val service: MessageService) {

    @GetMapping
    fun index(): List<Message> = service.findMessages()

    @GetMapping("/{id}")
    fun index(@PathVariable id: String): List<Message> = service.findMessageById(id)

    @PostMapping
    fun post(@RequestBody message: Message) {
        service.postMessage(message)
    }
}

@Service
class MessageService(val db: JdbcTemplate) {
    fun findMessages(): List<Message> = db.query("select * from messages") { rs, _ ->
        Message(rs.getString("id"), rs.getString("text"))
    }

    fun findMessageById(id: String): List<Message> = db.query("select * from messages where id = ?", id) { rs, _ ->
        Message(rs.getString("id"), rs.getString("text"))
    }

    fun postMessage(message: Message) = db.update("insert into messages values(?, ?)", message.id ?: message.text.uuid(), message.text)
}

data class Message(val id: String?, val text: String)

fun String.uuid(): String = UUID.nameUUIDFromBytes(this.encodeToByteArray()).toString()
