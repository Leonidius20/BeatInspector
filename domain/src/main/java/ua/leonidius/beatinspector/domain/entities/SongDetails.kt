package ua.leonidius.beatinspector.domain.entities

data class SongDetails(
    val name: String,
    val artists: Array<String>,
    val bpm: Double,
    val key: String, // todo: enum
    // todo: image cover
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongDetails

        if (name != other.name) return false
        if (!artists.contentEquals(other.artists)) return false
        if (bpm != other.bpm) return false
        return key == other.key
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + artists.contentHashCode()
        result = 31 * result + bpm.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }
}