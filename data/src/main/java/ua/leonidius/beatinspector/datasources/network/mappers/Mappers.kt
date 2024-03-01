package ua.leonidius.beatinspector.datasources.network.mappers

import ua.leonidius.beatinspector.datasources.network.dto.ArtistDto
import ua.leonidius.beatinspector.datasources.network.dto.FullArtistDto
import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.datasources.network.dto.TrackAudioAnalysisDto
import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.Song
import ua.leonidius.beatinspector.entities.SongSearchResult

fun TrackDto.toDomainObject(): SongSearchResult {
    val name = if (this.explicit) "$name \uD83C\uDD74" else name // "E" emoji
    return SongSearchResult(
        id = id,
        name = name,
        artists = artists.map { it.toDomainObject() },
        imageUrl = album.images[0].url,
        smallestImageUrl = album.images.minByOrNull { it.width * it.height }?.url,
    )
}

fun SearchResultsResponse.toListOfDomainObjects(): List<SongSearchResult> {
    return tracks.items.map { it.toDomainObject() }
}

fun assembleTrackDomainObject(
    baseInfo: SongSearchResult,
    details: TrackAudioAnalysisDto,
    genres: List<String>,
): Song {

    return Song(
        id = baseInfo.id,
        name = baseInfo.name,
        artist = baseInfo.artists.joinToString(", ") { it.name }, // todo: don't, just return as is and let ui layer handle it
        duration = details.duration,
        loudness = details.loudness,
        bpm = details.tempo,
        bpmConfidence = details.tempoConfidence,
        timeSignature = details.timeSignature,
        timeSignatureConfidence = details.timeSignatureConfidence,
        key = getKeyStringFromSpotifyValue(details.key, details.mode),
        keyConfidence = details.keyConfidence,
        modeConfidence = details.modeConfidence,
        genres = genres,
        albumArtUrl = baseInfo.imageUrl,
    )
}

private fun getKeyStringFromSpotifyValue(keyInt: Int, modeInt: Int): String {
    val key = when (keyInt) {
        0 -> "C"
        1 -> "C♯/D♭"
        2 -> "D"
        3 -> "D♯/E♭"
        4 -> "E"
        5 -> "F"
        6 -> "F♯/G♭"
        7 -> "G"
        8 -> "G♯/A♭"
        9 -> "A"
        10 -> "A♯/B♭"
        11 -> "B"
        else -> "?"
    }

    val mode = when (modeInt) {
        1 -> "Maj"
        0 -> "Min"
        else -> "?"

    }

    return "$key $mode"
}