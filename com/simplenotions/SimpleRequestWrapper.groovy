package com.simplenotions

import groovy.text.SimpleTemplateEngine


/**
 * Acts as a delegate for the closure that gets mapped to a route.
 */
class SimpleRequestWrapper {

    def httpExchange
    def templateEngine
    def status
    def renderStarted = false

    SimpleRequestWrapper(httpExchange) {
        this.httpExchange = httpExchange
        templateEngine = new SimpleTemplateEngine()
    }

    def setContentType(String contentType) {
        httpExchange.responseHeaders.set("Content-Type", contentType)
    }

    def setStatus(int httpStatus) {
        status = httpStatus
    }

    def renderTemplate(String templateFilename, Map model) {
        def file = new File("templates/${templateFilename}")
        def template = templateEngine.createTemplate(file.text).make(model)
        writeBody(template.toString())
    }

    def renderText(String text) {
        writeBody(text)
    }

    private void writeBody(String content) {
        if (!status) {
            setStatus(200)
            renderStarted = true
            httpExchange.sendResponseHeaders(200, 0)
        }
        httpExchange.responseBody.write(content.bytes)
    }

    def redirect(String uri) {
        httpExchange.responseHeaders.set("Location", uri)
        setStatus(302)
        httpExchange.sendResponseHeaders(302, 0)
    }

    def getHttpExchange() {
        return this.httpExchange
    }

}
