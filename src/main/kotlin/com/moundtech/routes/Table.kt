package com.moundtech.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.moundtech.Connection
import com.moundtech.PokerHand
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.lang.Exception
import java.util.*
import kotlin.collections.LinkedHashSet

fun Route.table() {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    authenticate("auth-jwt") {
        webSocket("/table") {
            println("Adding a user to the table")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                send("Hi $username! Thank you for connecting to the table, current player count is: ${connections.count()}")
                for (frame in incoming) {
                    when(frame) {
                        is Frame.Text -> {
                            val mapper = jacksonObjectMapper()
                            val rawText = frame.readText()
                            val decoded: PokerHand = mapper.readValue(rawText)
                            send("Your hand is ${decoded.values}")
                        }
                        else -> continue
                    }
                }
            } catch (e: Exception) {

            } finally {

            }
        }
    }
}