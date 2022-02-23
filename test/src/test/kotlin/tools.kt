package tools

import jodd.http.HttpRequest


class Caller {

    val host = "localhost"
    val port = 5056

    fun call(): HttpRequest {
        return HttpRequest()
                .host(host)
                .port(port)
    }
}