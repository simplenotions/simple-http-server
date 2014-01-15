package com.simplenotions

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler

class SimpleRequestHandler implements HttpHandler {

    def routes = []

    @Override
    void handle(HttpExchange exchange) {
        exchange.responseHeaders.set("Content-Type", "text/html")

        def uri = new URI(exchange.requestURI.toString())
        def result = lookupHandler(uri)

        if (!result) {
            println "No route found for ${uri}"
            exchange.sendResponseHeaders(404, 0)
            exchange.responseBody.close()
        } else {
            // default to OK status?
            def handler = result.handler
            handler.delegate = new SimpleRequestWrapper(exchange)
            handler.params = result.params
            handler.call() // or maybe handler() ?
            if (!handler.renderStarted) {
                exchange.sendResponseHeaders(200, 0)
            }
            exchange.responseBody.close()
        }
    }

    public void addRoute(String uri, Closure closure) {
        routes.add([(uri), closure])
    }

    public Map lookupHandler(URI uri) {
        Map result = [:]

        for (route in routes) {
            def matcher = uri.path =~ route[0]
            if (matcher.matches()) {
                result.put('handler', route[1])
                if (matcher[0].size() > 1) {
                    result.put("params", matcher[0][1..-1])
                }
                break
            }
        }

        return result
    }

}
