package tools

import jodd.http.HttpRequest
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.api.extension.ParameterResolver


class CallParameterResolver : ParameterResolver {

    @Throws(ParameterResolutionException::class)
    override fun supportsParameter(parameterContext: ParameterContext,
                                   extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == Caller::class.java
    }

    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(parameterContext: ParameterContext,
                                  extensionContext: ExtensionContext): Any {
        return Caller()
    }
}

class Caller {

    val host = "localhost"
    val port = 5056

    fun call(): HttpRequest {
        return HttpRequest()
                .host(host)
                .port(port)
    }
}