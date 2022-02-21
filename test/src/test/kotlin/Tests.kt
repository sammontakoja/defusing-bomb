import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jodd.http.HttpRequest
import jodd.http.HttpResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.random.Random.Default.nextInt

@Disabled
class Tests {

    @Test
    fun `Bomb created with given phone number`() {
        val phoneNumber = randomPhoneNumber()
        val createdBomb = createNewBomb(phoneNumber)
        assertEquals(false, createdBomb.isDetonated)
        assertEquals(phoneNumber, createdBomb.phoneNumber)
        assertEquals(null, createdBomb.detonationTime)
    }

    @Test
    fun `Bomb set to explode 1-3 seconds after ignition call`() {
        val phoneNumber = randomPhoneNumber()
        createNewBombCall(phoneNumber)

        val ignitionCallResponse = detonationCall(phoneNumber)
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
        detonationCall(phoneNumber)

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
    fun `Can cut green yellow and blue wires`() {
        val createdBomb = createNewBomb(randomPhoneNumber())
        detonationCall(createdBomb.phoneNumber)

        val greenWireCutResponse = cutWireCall(createdBomb.id, "green")
        assertEquals(200, greenWireCutResponse.statusCode())

        val yellowWireCutResponse = cutWireCall(createdBomb.id, "yellow")
        assertEquals(200, yellowWireCutResponse.statusCode())

        val blueWireCutResponse = cutWireCall(createdBomb.id, "blue")
        assertEquals(200, blueWireCutResponse.statusCode())
    }

    @Test
    fun `Bomb detonated when yellow wire cut first`() {
        val createdBomb = createNewBomb(randomPhoneNumber())
        detonationCall(createdBomb.phoneNumber)
        val wireCutResponse = cutWireCall(createdBomb.id, "yellow")
        assertEquals(200, wireCutResponse.statusCode())
        assertTrue(wireCutResponse.bodyText().contains("Kaboom!"))
        assertFalse(detonatedBombs().none { it.id == createdBomb.id })
    }

    @Test
    fun `Bomb detonated when blue wire cut first`() {
        val createdBomb = createNewBomb(randomPhoneNumber())
        detonationCall(createdBomb.phoneNumber)
        val wireCutResponse = cutWireCall(createdBomb.id, "blue")
        assertEquals(200, wireCutResponse.statusCode())
        assertTrue(wireCutResponse.bodyText().contains("Kaboom!"))
        assertFalse(detonatedBombs().none { it.id == createdBomb.id })
    }

    @Test
    fun `Bomb detonated when blue wire cut after green wire`() {
        val createdBomb = createNewBomb(randomPhoneNumber())
        detonationCall(createdBomb.phoneNumber)
        cutWireCall(createdBomb.id, "green")
        val wireCutResponse = cutWireCall(createdBomb.id, "blue")
        assertEquals(200, wireCutResponse.statusCode())
        assertTrue(wireCutResponse.bodyText().contains("Kaboom!"))
        assertFalse(detonatedBombs().none { it.id == createdBomb.id })
    }

    @Test
    fun `Bomb disarmed when wires are cut in following order green yellow and blue`() {
        val createdBomb = createNewBomb(randomPhoneNumber())
        detonationCall(createdBomb.phoneNumber)

        val greenWireCutResponse = cutWireCall(createdBomb.id, "green")
        assertEquals(200, greenWireCutResponse.statusCode())
        assertTrue(greenWireCutResponse.bodyText().contains("OK!"))

        val yellowWireCutResponse = cutWireCall(createdBomb.id, "yellow")
        assertEquals(200, yellowWireCutResponse.statusCode())
        assertTrue(yellowWireCutResponse.bodyText().contains("OK!"))

        val blueWireCutResponse = cutWireCall(createdBomb.id, "blue")
        assertEquals(200, blueWireCutResponse.statusCode())
        assertTrue(blueWireCutResponse.bodyText().contains("OK!"))

        assertEquals(true, detonatedBombs().none { it.id == createdBomb.id })
    }

    fun detonatedBombs(): List<Bomb> {
        val detonatedBombsCallResponse = detonatedBombsCall()
        assertEquals(200, detonatedBombsCallResponse.statusCode())
        return jacksonObjectMapper().readValue(detonatedBombsCallResponse.bodyText())
    }

    fun createNewBombCall(phoneNumber: String): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("POST")
                .path("bombs/phonenumber/$phoneNumber")
                .send()
    }

    fun createNewBomb(phoneNumber: String): Bomb {
        val response = createNewBombCall(phoneNumber)
        assertEquals(201, response.statusCode())
        return jacksonObjectMapper().readValue(response.bodyText())
    }

    fun detonationCall(phoneNumber: String): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("PUT")
                .path("bombs/ignite/$phoneNumber")
                .send()
    }

    fun cutWireCall(id: Int, colour: String): HttpResponse {
        return HttpRequest()
                .host(host)
                .port(port)
                .method("PUT")
                .path("bombs/$id/cutwire/$colour")
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