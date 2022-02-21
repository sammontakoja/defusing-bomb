import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jodd.http.HttpRequest
import jodd.http.HttpResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.random.Random.Default.nextInt

@Disabled
class Tests {

    @Test
    fun `Bomb created with given phone number`() {
        val phoneNumber = randomPhoneNumber()
        val response = createNewBombCall(phoneNumber)
        println(response)
        assertEquals(201, response.statusCode())
        val bomb: Bomb = jacksonObjectMapper().readValue(response.bodyText())
        assertEquals(false, bomb.isDetonated)
        assertEquals(phoneNumber, bomb.phoneNumber)
        assertEquals(null, bomb.detonationTime)
    }

    @Test
    fun `Bomb set to explode 1-3 seconds after ignition call`() {
        val phoneNumber = randomPhoneNumber()
        createNewBombCall(phoneNumber)

        val ignitionCallResponse = bombIgnitionCall(phoneNumber)
        val bomb: Bomb = jacksonObjectMapper().readValue(ignitionCallResponse.bodyText())

        if (bomb.detonationTime != null) {
            val onceSecondsAhead = LocalDateTime.now().plusSeconds(1)
            val threeSecondsAhead = onceSecondsAhead.plusSeconds(2)
            assertEquals(
                    true, onceSecondsAhead.isBefore(bomb.detonationTime),
                    "Bomb explode ${bomb.detonationTime}, should explode after $onceSecondsAhead"
            )
            assertEquals(
                    true, bomb.detonationTime.isBefore(threeSecondsAhead),
                    "Bomb explode ${bomb.detonationTime}, should explode before $threeSecondsAhead"
            )
        } else
            throw Exception("Expected non empty detonationTime")
    }

    @Test
    fun `Bomb detonated 2100 milliseconds after ignition call`() {
        val phoneNumber = randomPhoneNumber()
        createNewBombCall(phoneNumber)
        bombIgnitionCall(phoneNumber)

        Thread.sleep(2100)

        val detonatedBombsCallResponse = detonatedBombsCall()
        println(detonatedBombsCallResponse)
        assertEquals(200, detonatedBombsCallResponse.statusCode())
        val bombs: List<Bomb> = jacksonObjectMapper().readValue(detonatedBombsCallResponse.bodyText())
        assertEquals(true, bombs.isNotEmpty())
    }

    fun randomPhoneNumber(): String {
        val sevenRandomDigits = (1..7)
                .map { i -> nextInt(9) }
                .joinToString("")
        return "040$sevenRandomDigits"
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

    fun createNewBombCall(phoneNumber: String): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("POST")
                .path("bombs/phonenumber/$phoneNumber")
                .send()
    }

    fun bombIgnitionCall(phoneNumber: String): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("PUT")
                .path("bombs/ignite/$phoneNumber")
                .send()
    }

    fun detonatedBombsCall(): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("GET")
                .path("bombs/detonated")
                .send()
    }

    val host = "localhost"
    val port = 5056

}