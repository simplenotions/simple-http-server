import com.simplenotions.SimpleHttpServer

def server = new SimpleHttpServer()

server.with {
    // register routes, first match wins
    route("/books/(\\d+)", new demo.handlers.BookHandler().run().bookHandler)

    route("/books") {
        renderText("Book List")
    }
    
    route("/bookList") {
        // redirect supported
        redirect("/books")
    }
    
    route("/authors") {
        renderText("Author List")
    }
}

server.route("/") {
    renderTemplate('index.html', [message:"Hello Everybody!"])
}

// run the service
server.start()
