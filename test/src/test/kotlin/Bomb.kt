import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.util.StdConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class Bomb(
        val id: Int,
        val isDetonated: Boolean,
        val phoneNumber: String,
        @JsonDeserialize(converter = LongToLocalDateTimeConverter::class)
        val detonationTime: LocalDateTime?,
        val wiresCut: String
)

class LongToLocalDateTimeConverter : StdConverter<Long, LocalDateTime>() {
    override fun convert(value: Long): LocalDateTime {
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}