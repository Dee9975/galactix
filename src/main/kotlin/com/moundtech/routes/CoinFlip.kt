package com.moundtech.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.moundtech.Connection
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import java.util.*
import kotlin.collections.LinkedHashSet

fun Route.coinFlip() {
    val client = KMongo.createClient()
    val database = client.getDatabase("galactic-poker")

    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    authenticate("auth-jwt") {
        route("/coin-flip") {
            val col = database.getCollection<CoinTable>()
            get("/rooms") {
                try {
                    val rooms = col.find<CoinTable>().toList()
                    call.respond(rooms)
                } catch (e: Exception) {
                    call.respondText(
                        "{\"error\": \"Error creating a room: ${e.localizedMessage}\"",
                        status = HttpStatusCode.BadRequest, contentType = ContentType.Application.Json
                    )
                }

            }

            post("/create-room", ) {
                val room = call.receive<CoinTable>()
                try {
                    col.insertOne(room)
                    call.respond(room)
                } catch (e: Exception) {
                    call.respondText(
                        "{\"error\": \"Error creating a room: ${e.localizedMessage}\"",
                        status = HttpStatusCode.BadRequest, contentType = ContentType.Application.Json
                    )
                }
            }

            webSocket("{id}") {

            }
        }
    }
}

@Serializable
data class User(
    val id: String = ObjectId().toHexString(),
    val username: String,
)

@Serializable
data class CoinTable(
    val id: String = ObjectId().toHexString(),
    val name: String,
    val players: List<User>?,
    val spectators: List<User>?
)

