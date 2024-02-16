package ua.leonidius.beatinspector.datasources.network.mappers

import ua.leonidius.beatinspector.datasources.network.dto.ArtistDto
import ua.leonidius.beatinspector.datasources.network.dto.SearchResultsResponse
import ua.leonidius.beatinspector.datasources.network.dto.TrackDto
import ua.leonidius.beatinspector.entities.Artist
import ua.leonidius.beatinspector.entities.SongSearchResult

fun TrackDto.toDomainObject(): SongSearchResult {
    return SongSearchResult(
        id = id,
        name = name,
        artists = artists.map { it.toDomainObject() },
        imageUrl = album.images[0].url
    )
}

fun ArtistDto.toDomainObject(): Artist {
    return Artist(
        id = id,
        name = name
    )
}

fun SearchResultsResponse.toListOfDomainObjects(): List<SongSearchResult> {
    return tracks.items.map { it.toDomainObject() }
}