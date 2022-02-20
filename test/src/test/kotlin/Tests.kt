import jodd.http.HttpRequest
import jodd.http.HttpResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class Tests {

    @Test
    fun `Bomb start to tick after phone call to number 042 486 6247`() {
        val phoneCallResponse = phoneCall("042 486 6247")
        assertEquals(200, phoneCallResponse.statusCode())
        assertEquals("ticktock", phoneCallResponse.bodyText())
    }

    @Test
    fun `Bomb disarmed when wires are cut in following order green yellow and blue`() {
    }

    @Test
    fun `Bomb explode after two seconds`() {
    }

    @Test
    fun `Bomb explode when yellow wire is cut first`() {
    }

    @Test
    fun `Bomb explode when blue wire is cut first`() {
    }

    @Test
    fun `Bomb explode when green wire is cut first and then blue`() {
    }

    fun phoneCall(number: String): HttpResponse {
        return HttpRequest()
            .host(host)
            .port(port)
            .method("POST")
            .path("phonecall/$number")
            .send()
    }

    val host = "localhost"
    val port = 8080

}