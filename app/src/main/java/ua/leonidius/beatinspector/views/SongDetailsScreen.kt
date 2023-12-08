package ua.leonidius.beatinspector.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    songId: SongId?
) {
    Column(modifier) {
        Text("This is supposed to represent a song with id of $songId")
    }
}