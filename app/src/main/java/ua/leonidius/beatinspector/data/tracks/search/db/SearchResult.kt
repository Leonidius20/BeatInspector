package ua.leonidius.beatinspector.data.tracks.search.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * this table backs a list of search results.
 * although it's possible that is doesn't make any fucking sense to store
 * Tracks (track shelf data) in their own table, because tracks in this app
 * don't exist all by themselves, actually they can only exist as a search
 * result or a member of a playlist (although theoretically we can add the
 * ability to open spotify links in this app, such as track links, and taking
 * ppl straight to the track details screen, which would justify caching
 * tracks as a separate table, i suppose (but even then the same info
 * could be embedded into track audio analysis)).
 *
 * the downsides of having this separate table are:
 * 1) the fact that we will store titles and cover urls for all tracks that
 * the user has ever seen in the app (incl. in search results), even though
 * for a lot of tracks the user will never see them again. They will become
 * dangling records without ownership (neither any playlist nor the search
 * owns them). This will be a waste of memory and hard to deal with
 *
 * 2) there was some other downside but i don't remember what is was
 *
 * Overall, we should delete the TrackShelfInfo table and store the song
 * titles/cover urls embedded right into the search results and playlist
 * contents tables. todo: do just that.
 *
 * However, the Artists and their genres should be stored in a separate table, because they
 * can be shared between tracks, and old artist's data can be reused for
 * new tracks. So we will have to join the artists table to Searchresult
 */
@Entity(tableName = "search_results")
data class SearchResult(

    @PrimaryKey @ColumnInfo(name = "track_id") val trackId: String

)
