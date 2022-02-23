package tools

import jodd.http.HttpRequest
import jodd.http.HttpResponse
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths

class CallerWithDockerizedRestApi:  BeforeAllCallback {

    var host = ""
    var port: Int = 0

    override fun beforeAll(p0: ExtensionContext?) {
        fun startRestApiContainer(): GenericContainer<Nothing> {
            val restApiContainer: GenericContainer<Nothing> =
                    GenericContainer<Nothing>(ImageFromDockerfile("defusing-bomb-rest-api")
                            .withDockerfile(Paths.get(System.getProperty("user.dir"), "../rest-api/Dockerfile")))
                            .withExposedPorts(5056)
            restApiContainer.withEnv("ASPNETCORE_URLS", "http://+:5056")
            restApiContainer.start()
            return restApiContainer
        }
        val startedRestApiContainer = startRestApiContainer()
        host = startedRestApiContainer.host
        port = startedRestApiContainer.firstMappedPort
    }

    fun call(request: HttpRequest): HttpResponse {
        val requestWithEnvironmentSettings = request
                .host(host)
                .port(port)
        return try {
            return requestWithEnvironmentSettings.send()
        } catch (e: Exception) {
            throw Exception("Error with request: " + requestWithEnvironmentSettings, e)
        }
    }

}

class Caller:  BeforeAllCallback {

    var host = ""
    var port: Int = 0

    override fun beforeAll(p0: ExtensionContext?) {
        host = "localhost"
        port = 5056
    }

    fun call(request: HttpRequest): HttpResponse {
        val requestWithEnvironmentSettings = request
                .host(host)
                .port(port)
        return try {
            return requestWithEnvironmentSettings.send()
        } catch (e: Exception) {
            throw Exception("Error with request: " + requestWithEnvironmentSettings, e)
        }
    }

}