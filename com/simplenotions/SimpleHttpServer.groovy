package com.simplenotions

import com.sun.net.httpserver.HttpServer
import java.util.concurrent.Executors

/**
 * A simple HTTP server build on top of Java's HttpServer.
 * Inspired by various micro frameworks.
 */
class SimpleHttpServer {
    def server
    def port
    def requestHandler
    def threads = 0

    SimpleHttpServer(int port=8080) {
        this.port = port
        int backlog = 0 // use default socket backlog

        println "Creating Simple HTTP Server"
        server = HttpServer.create(new InetSocketAddress(port), backlog)

        if (threads < 1) {
            // unbounded threads
            server.setExecutor(Executors.newCachedThreadPool())
        } else {
            server.setExecutor(Executors.newFixedThreadPool(threads))
        }

        requestHandler = new SimpleRequestHandler()
    }

    def route(String uri, Closure closure) {
        requestHandler.addRoute(uri, closure)
    }

    def start() {
        println "Starting service"
        server.createContext("/", requestHandler)
        server.start()
        println "Service listeing on ${port}"
    }
}
