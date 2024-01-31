package ua.leonidius.beatinspector.views

import android.content.res.Configuration
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor
import ua.leonidius.beatinspector.viewmodels.SongDetailsViewModel

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    detailsViewModel: SongDetailsViewModel = viewModel(factory = SongDetailsViewModel.Factory),
    windowSize: WindowSizeClass,
) {
    // todo: why do smart casts work in the other composable, but not inside of this one?
    SongDetailsScreen(modifier, detailsViewModel.uiState)
}

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    uiState: SongDetailsViewModel.UiState = SongDetailsViewModel.UiState.Loading,
) {
    when (uiState) {
        is SongDetailsViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }

        is SongDetailsViewModel.UiState.Error -> {
            Text(text = stringResource(id = uiState.errorMsgId))
        }

        is SongDetailsViewModel.UiState.Loaded -> {
            with(uiState) {
                if (failedArtists.isNotEmpty()) {
                    // todo: better way to display this
                    Text(text = "Failed artists: $failedArtists")
                }

                when (LocalConfiguration.current.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        SongDetailsLandscapeScreen(
                            modifier,
                            name = title,
                            artistString = artists,
                            bpm = bpm,
                            key = key,
                            timeSignature = timeSignatureOver4,
                            loudness = loudness,
                            genres = genres,
                            albumArtUrl = albumArtUrl,
                        )
                    }

                    else -> {
                        SongDetailsPortraitScreen(
                            modifier,
                            name = title,
                            artistString = artists,
                            bpm = bpm,
                            key = key,
                            timeSignature = timeSignatureOver4,
                            loudness = loudness,
                            genres = genres,
                            albumArtUrl = albumArtUrl,
                        )
                    }

                }

            }
        }
    }
}

@Composable
fun SongDetailsPortraitScreen(
    modifier: Modifier = Modifier,
    name: String,
    artistString: String,
    bpm: String,
    key: String,
    timeSignature: Int,
    loudness: String,
    genres: String,
    albumArtUrl: String,
) {
    BoxWithConstraints {
        val boxScope = this

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
                var palette by remember { mutableStateOf<Palette?>(null) }

                if (palette != null) {
                    ChangeStatusBarColor(palette?.darkVibrantSwatch?.rgb ?: Color.Transparent.toArgb())
                }

                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(albumArtUrl)
                        .size(coil.size.Size.ORIGINAL)
                        .allowHardware(false) // so that palette can be generated from the image
                        .build(),
                    onSuccess = { state ->
                        val bitmap = state.result.drawable.toBitmap()
                        palette = Palette.from(bitmap).generate()
                    },
                    contentScale = ContentScale.Crop)

                Image(
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
                    painter = painter,
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

                    Text(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 25.dp),
                        text = artistString,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Light,
                        //color = MaterialTheme.colorScheme.inverseOnSurface,
                    )

                   // Spacer(modifier = Modifier.height(10.dp))

                    val cardColors = palette?.lightMutedSwatch?.let { Color(it.rgb) }?.let {
                        CardDefaults.cardColors(
                            containerColor = it,
                        )
                    } ?: CardDefaults.cardColors()

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        InfoCard(
                            Modifier.weight(1F),
                            colors = cardColors,
                            title = "bpm", data = bpm,
                        )
                        Spacer(Modifier.width(10.dp))
                        InfoCard(
                            Modifier.weight(1F),
                            colors = cardColors,
                            title = "key", data = key
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        InfoCard(
                            Modifier.weight(1F),
                            colors = cardColors,
                            title = "time signature", data = "$timeSignature/4"
                        )
                        Spacer(Modifier.width(10.dp))
                        InfoCard(
                            Modifier.weight(1F),
                            colors = cardColors,
                            title = "loudness", data = loudness,
                        )
                    }
                    Spacer(Modifier.height(10.dp))

                    CompactInfoCard(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        colors = cardColors,
                        title = "artists' genres", data = genres,
                        emptyReplacementText = stringResource(R.string.no_data),
                    )
                }

            }


        }
    }

}

@Composable
fun SongDetailsLandscapeScreen(
    modifier: Modifier = Modifier,
    name: String,
    artistString: String,
    bpm: String,
    key: String,
    timeSignature: Int,
    loudness: String,
    genres: String,
    albumArtUrl: String,
) {
    Row(modifier) {

        var palette by remember { mutableStateOf<Palette?>(null) }

        if (palette != null) {
            ChangeStatusBarColor(palette?.darkVibrantSwatch?.rgb ?: Color.Transparent.toArgb())
        }

        val cardColors = palette?.lightMutedSwatch?.let { Color(it.rgb) }?.let {
            CardDefaults.cardColors(
                containerColor = it,
            )
        } ?: CardDefaults.cardColors()

        BoxWithConstraints(
            Modifier
                .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                .clip(RoundedCornerShape(10.dp))
        ) { // image with title and artists
            val boxScope = this

            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(albumArtUrl)
                    .size(coil.size.Size.ORIGINAL)
                    .allowHardware(false) // so that palette can be generated from the image
                    .build(),
                onSuccess = { state ->
                    val bitmap = state.result.drawable.toBitmap()
                    palette = Palette.from(bitmap).generate()
                },
                contentScale = ContentScale.Crop)


            Image(
                modifier = Modifier
                    .height(boxScope.maxHeight)
                    .width(boxScope.maxHeight)
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
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )


            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(boxScope.maxHeight * 0.9F)
            ) {
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
                    text = artistString,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Light,
                    //color = MaterialTheme.colorScheme.inverseOnSurface,
                )
            }
        }

        Column(
            Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .verticalScroll(rememberScrollState())
        ) {// info cards


            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                InfoCard(
                    Modifier.weight(1F),
                    colors = cardColors,
                    title = "bpm", data = bpm,
                    isLandScape = true,
                )
                Spacer(Modifier.width(10.dp))
                InfoCard(
                    Modifier.weight(1F),
                    colors = cardColors,
                    title = "key", data = key,
                    isLandScape = true,
                )
            }
            Spacer(Modifier.height(10.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                InfoCard(
                    Modifier.weight(1F),
                    colors = cardColors,
                    title = "time signature", data = "$timeSignature/4",
                    isLandScape = true,
                )
                Spacer(Modifier.width(10.dp))
                InfoCard(
                    Modifier.weight(1F),
                    colors = cardColors,
                    title = "loudness", data = loudness,
                    isLandScape = true,
                )
            }
            Spacer(Modifier.height(10.dp))

            CompactInfoCard(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                colors = cardColors,
                title = "artists' genres", data = genres,
                emptyReplacementText = stringResource(R.string.no_data),
            )
        }
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    data: String,
    colors: CardColors = CardDefaults.cardColors(),
    isLandScape: Boolean = false,
) {
    Card(
        modifier = if (isLandScape) modifier.height(120.dp) else modifier.height(150.dp),
        // todo: calculate height based on screen size, pass it with modifier
        colors = colors,
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
    emptyReplacementText: String,
    colors: CardColors = CardDefaults.cardColors(),
) {
    Card(
        modifier,
        colors = colors,
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
                if (data.isNotEmpty()) {
                    Text(text = data,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                    )
                } else {
                    Text(
                        text = "< $emptyReplacementText >",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F),
                    )
                }
            }

        }
    }
}

@Composable
@Preview("SearchScreenPortraitPreview", widthDp = 320, showBackground = true)
fun SongDetailsPortraitScreenPreview() {
    SongDetailsPortraitScreen(
        name = "Pacifier",
        artistString = listOf("Baby Gronk", "Super Sus").joinToString(", "),
        bpm = "420",
        key = "C Maj",
        timeSignature = 4,
        loudness = "-5.2 db",
        genres = "Hip Hop, Rap",
        albumArtUrl = "https://fakeimg.pl/640x640?text=test&font=lobster",
    )
}

@Composable
@Preview("SearchScreenLandscapePreview", widthDp = 640, showBackground = true)
fun SongDetailsLandscapeScreenPreview() {
    SongDetailsLandscapeScreen(
        name = "Pacifier",
        artistString = listOf("Baby Gronk", "Super Sus").joinToString(", "),
        bpm = "420",
        key = "C Maj",
        timeSignature = 4,
        loudness = "-5.2 db",
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