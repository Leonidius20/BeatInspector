package ua.leonidius.beatinspector.views

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import ua.leonidius.beatinspector.viewmodels.SongDetailsViewModel

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    detailsViewModel: SongDetailsViewModel = viewModel(factory = SongDetailsViewModel.Factory),
) {
    with(detailsViewModel.songDetails) {
        SongDetailsScreen(
            modifier,
            name = title,
            artists = listOf(artist), // todo: decide if this should be a list or not
            bpm = bpm,
            key = key,
            genres = genres,
            albumArtUrl = albumArtUrl,
        )
    }
}

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    name: String,
    artists: List<String>,
    bpm: String,
    key: String,
    genres: String,
    albumArtUrl: String,
) {

    // todo: make it scrollable
    BoxWithConstraints {
        val boxScope = this

        // image
        // column with rest




        Column(
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    // .height(boxScope.maxHeight * 0.3F)
                    // .fillMaxHeight(0.3F)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .fadingEdge(
                            Brush.verticalGradient(
                                0F to MaterialTheme.colorScheme.surface.copy(alpha = 1F), // from top to title

                              //  0.25F to MaterialTheme.colorScheme.surface.copy(alpha = 0.3F), // from title to lowest part

                                 0.4F to MaterialTheme.colorScheme.surface.copy(alpha = 0.35F), // from title to lowest part
                                //0.5F to MaterialTheme.colorScheme.surface.copy(alpha = 0.5F),

                                0.8F to MaterialTheme.colorScheme.surface.copy(alpha = 0.04F),  // lowest part (behind cards)
                                1F to MaterialTheme.colorScheme.surface.copy(alpha = 0F),
                            )
                        ),
                    model = albumArtUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )



                Column(
                   // modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    Spacer(modifier = Modifier.height(boxScope.maxHeight * 0.2F))

                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, top = 25.dp, bottom = 10.dp),
                        text = name,
                        style = MaterialTheme.typography.headlineLarge,
                        //color = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                    // todo: do the joining in a different layer (presentation)
                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 25.dp),
                        text = artists.joinToString(", "),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Light,
                        //color = MaterialTheme.colorScheme.inverseOnSurface,
                    )

                   // Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        InfoCard(
                            Modifier.weight(1F),
                            title = "bpm", data = bpm
                        )
                        Spacer(Modifier.width(10.dp))
                        InfoCard(
                            Modifier.weight(1F),
                            title = "key", data = key
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    CompactInfoCard(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        title = "artists' genres", data = genres
                    )
                }

            }


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
        modifier.height(150.dp)
    ) {
        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 17.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.5.sp,
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(bottom = 23.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(text = data,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

        }
    }
}

@Composable
fun CompactInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    data: String,
) {
    Card(
        modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
        ) {
            Text(text = title,
                style = MaterialTheme.typography.labelLarge,
                fontSize = 17.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.5.sp,)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 10.dp)
                //horizontalArrangement = Arrangement.Center,
                //verticalAlignment = Alignment.CenterVertically

            ) {
                Text(text = data,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                )
            }

        }
    }
}

@Composable
@Preview("SearchScreenPreview", widthDp = 320, showBackground = true)
fun SongDetailsScreenPreview() {
    SongDetailsScreen(
        name = "Pacifier",
        artists = listOf("Baby Gronk", "Super Sus"),
        bpm = "420",
        key = "C Maj",
        genres = "Hip Hop, Rap",
        albumArtUrl = "https://fakeimg.pl/640x640?text=test&font=lobster",
    )
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }