package ua.leonidius.beatinspector.data.tracks.shared.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * this represents the title and the artists and the small & big cover
 * images of a song. This data will be updated every time someone fetches
 * a list of items (search, playlist content etc) and a certain song happens
 * to be there (bc if fresh data is received this way every time, might as well
 * update the data). However, this data will never be invalidated on its own
 * (bc song data doesn't change usually)
 * and as such does not have a timestamp.
 *
 * the playlist contents, search results will be stored in other tables
 * with reference to this one.
 *
 * the playlist membership data will be timestamped and invalidated
 *
 * the search results will not be timestamped, instead it will be invalidated every time
 */

data class SongSearchResult(
    @PrimaryKey override val id: String,
    val name: String,

    val artists: List<Artist>, // todo: how do we represent the artists?
    @ColumnInfo(name = "is_explicit") val isExplicit: Boolean,
    @ColumnInfo(name = "big_image_url") val imageUrl: String?,
    @ColumnInfo(name = "small_image_url") val smallestImageUrl: String? = null,


): ua.leonidius.beatinspector.data.shared.domain.SearchResult
