package ua.leonidius.beatinspector.features.details.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import ua.leonidius.beatinspector.R
import ua.leonidius.beatinspector.features.details.viewmodels.SongDetailsViewModel
import ua.leonidius.beatinspector.features.shared.ui.LoadingScreen
import ua.leonidius.beatinspector.ui.theme.ChangeStatusBarColor

@Composable
fun SongDetailsScreen(
    modifier: Modifier = Modifier,
    detailsViewModel: SongDetailsViewModel = hiltViewModel(),
    onOpenInSpotifyButtonClick: (String) -> Unit,
) {
    // todo: why do smart casts work in the other composable, but not inside of this one?

    SongDetailsScreenI(
        modifier,
        detailsViewModel.uiState,
        onOpenInSpotifyButtonClick,
    )
}

@Composable
fun SongDetailsScreenI(
    modifier: Modifier = Modifier,
    uiState: SongDetailsViewModel.UiState = SongDetailsViewModel.UiState.Loading,
    onOpenInSpotifyButtonClick: (String) -> Unit,
) {
    when (uiState) {
        is SongDetailsViewModel.UiState.Loading -> LoadingScreen()

        is SongDetailsViewModel.UiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Column(

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = uiState.errorMsgId))
                    Card(Modifier.fillMaxWidth()) {
                        Text(text = uiState.errorAdditionalInfo)
                    }
                }

            }

        }

        is SongDetailsViewModel.UiState.Loaded -> {
            with(uiState) {

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
                            onOpenInSpotifyButtonClick = { onOpenInSpotifyButtonClick(songId) },
                            isSpotifyInstalled,
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
                            onOpenInSpotifyButtonClick = { onOpenInSpotifyButtonClick(songId) },
                            isSpotifyInstalled,
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
    onOpenInSpotifyButtonClick: () -> Unit,
    isSpotifyInstalled: Boolean,
) {
    BoxWithConstraints {
        val boxScope = this

        Column(
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            var palette by remember { mutableStateOf<Palette?>(null) }

            if (palette != null) {
                ChangeStatusBarColor(palette?.darkVibrantSwatch?.rgb ?: Color.Transparent.toArgb())
            }

            val swatch = palette?.let {
                if (isSystemInDarkTheme()) it.darkMutedSwatch else it.lightMutedSwatch
            }


            val cardColors = swatch?.let {
                CardDefaults.cardColors(
                    containerColor = Color(it.rgb),
                    // contentColor = Color(it.bodyTextColor)
                )
            } ?: CardDefaults.cardColors()

            Column(
                // modifier = Modifier.align(Alignment.BottomStart)
            ) {
                // Spacer(modifier = Modifier.height(boxScope.maxHeight * 0.2F))

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

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    // .height(boxScope.maxHeight * 0.3F)
                    // .fillMaxHeight(0.3F)
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {


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
                    /*.fadingEdge(
                        Brush.verticalGradient(
                            0F to MaterialTheme.colorScheme.surface.copy(alpha = 1F), // from top to title

                            //  0.25F to MaterialTheme.colorScheme.surface.copy(alpha = 0.3F), // from title to lowest part

                            0.4F to MaterialTheme.colorScheme.surface.copy(alpha = 0.35F), // from title to lowest part
                            //0.5F to MaterialTheme.colorScheme.surface.copy(alpha = 0.5F),

                            0.8F to MaterialTheme.colorScheme.surface.copy(alpha = 0.04F),  // lowest part (behind cards)
                            1F to MaterialTheme.colorScheme.surface.copy(alpha = 0F),
                        )
                    )*/,
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )





            }

            val buttonColors = swatch?.let { Color(it.rgb) }?.let {
                ButtonDefaults.buttonColors(
                    containerColor = it,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            } ?: ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            )

            OpenInSpotifyButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp, bottom = 20.dp),
                colors = buttonColors,
                onClick = onOpenInSpotifyButtonClick,
                isSpotifyInstalled = isSpotifyInstalled,
            )

        }
    }

}

@Composable
fun OpenInSpotifyButton(
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
    isSpotifyInstalled: Boolean,
) {
    Button(
        modifier = modifier.height(60.dp),
        colors = colors,
        onClick = onClick
    ) {
        if (isSpotifyInstalled) {
            Text("PLAY ON")
            Icon(
                modifier = Modifier
                    .height(30.dp)
                    .padding(start = 15.dp),
                painter = painterResource(R.drawable.spotify_full_logo_black),
                contentDescription = "Spotify")
        } else {
            Text("GET")


            Icon(
                modifier = Modifier
                    .height(30.dp)
                    .padding(start = 15.dp, end = 15.dp),
                painter = painterResource(R.drawable.spotify_full_logo_black),
                contentDescription = "Spotify")
            Text("FREE")
        }
    }
}

@Preview
@Composable
fun OpenInSpotifyButtonPreview() {
    OpenInSpotifyButton(
        onClick = {},
        isSpotifyInstalled = true,
    )
}

@Preview
@Composable
fun GetSpotifyFreeButtonPreview() {
    OpenInSpotifyButton(
        onClick = {},
        isSpotifyInstalled = false,
    )
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
    onOpenInSpotifyButtonClick: () -> Unit,
    isSpotifyInstalled: Boolean,
) {
    Row(modifier) {

        var palette by remember { mutableStateOf<Palette?>(null) }

        if (palette != null) {
            ChangeStatusBarColor(palette?.darkVibrantSwatch?.rgb ?: Color.Transparent.toArgb())
        }

        val swatch = palette?.let {
            if (isSystemInDarkTheme()) it.darkMutedSwatch else it.lightMutedSwatch
        }

        val cardColors = swatch?.let { Color(it.rgb) }?.let {
            CardDefaults.cardColors(
                containerColor = it,
            )
        } ?: CardDefaults.cardColors()

        val buttonColors = swatch?.let { Color(it.rgb) }?.let {
            ButtonDefaults.buttonColors(
                containerColor = it,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        } ?: ButtonDefaults.buttonColors()

        BoxWithConstraints(
            Modifier
                .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                // .clip(RoundedCornerShape(10.dp))
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

            Column(
                Modifier.verticalScroll(rememberScrollState())
            ) {

                Column(
                    modifier = Modifier
                        // .align(Alignment.BottomStart)
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

                Image(
                    modifier = Modifier
                        .height(boxScope.maxHeight)
                        .width(boxScope.maxHeight)
                        /*.fadingEdge(
                            Brush.verticalGradient(
                                0F to MaterialTheme.colorScheme.surface.copy(alpha = 1F), // from top to title

                                //  0.25F to MaterialTheme.colorScheme.surface.copy(alpha = 0.3F), // from title to lowest part

                                0.4F to MaterialTheme.colorScheme.surface.copy(alpha = 0.35F), // from title to lowest part
                                //0.5F to MaterialTheme.colorScheme.surface.copy(alpha = 0.5F),

                                0.8F to MaterialTheme.colorScheme.surface.copy(alpha = 0.04F),  // lowest part (behind cards)
                                1F to MaterialTheme.colorScheme.surface.copy(alpha = 0F),
                            )
                        )*/,
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
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



            OpenInSpotifyButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp, bottom = 10.dp),
                colors = buttonColors,
                onClick = onOpenInSpotifyButtonClick,
                isSpotifyInstalled = isSpotifyInstalled,
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
fun SearchScreenPreview() {
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val artists = remember {
        listOf("Baby Gronk", "Super Sus", "Some Body", "Micheal Hunt", "Cunning Linguist").joinToString(", ")
    }

    val name = "Pacifier Very Long Text That Should Not Be Truncated As per the Terms"

    if (isLandscape) {
        SongDetailsLandscapeScreen(
            name = name,
            artistString = artists,
            bpm = "420",
            key = "C Maj",
            timeSignature = 4,
            loudness = "-5.2 db",
            genres = "Hip Hop, Rap",
            albumArtUrl = "https://fakeimg.pl/640x640?text=test&font=lobster",
            onOpenInSpotifyButtonClick = {},
            isSpotifyInstalled = true,
        )
    } else {
        SongDetailsPortraitScreen(
            name = name,
            artistString = artists,
            bpm = "420",
            key = "C Maj",
            timeSignature = 4,
            loudness = "-5.2 db",
            genres = "Hip Hop, Rap",
            albumArtUrl = "https://fakeimg.pl/640x640?text=test&font=lobster",
            onOpenInSpotifyButtonClick = {},
            isSpotifyInstalled = true,
        )
    }
}

@Composable
@Preview("SearchScreenPortraitPreview", widthDp = 320, showBackground = true)
fun SongDetailsPortraitScreenPreview() {
    SongDetailsPortraitScreen(
        name = "Pacifier Very Long Text That Should Not Be Truncated As per the Terms",
        artistString = listOf("Baby Gronk", "Super Sus", "Some Body", "Micheal Hunt", "Cunning Linguist").joinToString(", "),
        bpm = "420",
        key = "C Maj",
        timeSignature = 4,
        loudness = "-5.2 db",
        genres = "Hip Hop, Rap",
        albumArtUrl = "https://fakeimg.pl/640x640?text=test&font=lobster",
        onOpenInSpotifyButtonClick = {},
        isSpotifyInstalled = true,
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
        onOpenInSpotifyButtonClick = {},
        isSpotifyInstalled = true,
    )
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }