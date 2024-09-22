package ua.leonidius.beatinspector.data.tracks.shared.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// maybe we should just query select genreid, name from artist_genres join genres where artistId = ?

/*data class ArtistWithTheirGenres(
    @Embedded val artist: Artist,
    @Relation(
        parentColumn = "id", // parent primary key - artists.id
        entityColumn = "id", // child PK - genres.id
        associateBy = Junction(ArtistGenreAssociation::class)
    )
    val genres: List<Genre>
)*/