package ua.leonidius.beatinspector.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.leonidius.beatinspector.viewmodels.SongDetailsViewModel

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    detailsViewModel: SongDetailsViewModel = viewModel(factory = SongDetailsViewModel.Factory),
    songId: SongId?
) {
    LaunchedEffect(key1 = true) {
        detailsViewModel.loadSongDetails(songId!!)
    }

    with(detailsViewModel.songDetails) {
        SongDetailsScreen(
            modifier,
            name = name,
            artists = artists,
            bpm = bpm.toString(), // todo: tostring in different layer
            key = key
        )
    }
}

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    name: String,
    artists: Array<String>,
    bpm: String,
    key: String
) {
    Column(modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 25.dp, bottom = 10.dp),
            text = name,
            style = MaterialTheme.typography.titleLarge
        )
        // todo: do the joining in a different layer (presentation)
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 25.dp),
            text = artists.joinToString(", "),
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            InfoCard(
                Modifier.weight(1F),
                title = "BPM", data = bpm
            )
            Spacer(Modifier.width(10.dp))
            InfoCard(
                Modifier.weight(1F),
                title = "Key", data = key
            )
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    data: String,
) {
    Card(
        modifier.height(150.dp),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(data, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
@Preview("SearchScreenPreview", widthDp = 320, showBackground = true)
fun SongDetailsScreenPreview() {
    SongDetailsScreen(
        name = "Pacifier",
        artists = arrayOf("Baby Gronk", "Super Sus"),
        bpm = "420",
        key = "C Maj"
    )
}