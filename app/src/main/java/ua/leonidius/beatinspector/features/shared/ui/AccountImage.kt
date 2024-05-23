package ua.leonidius.beatinspector.features.shared.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.sebaslogen.resaca.hilt.hiltViewModelScoped
import ua.leonidius.beatinspector.features.shared.viewmodel.AccountImageViewModel
import ua.leonidius.beatinspector.features.shared.viewmodel.PfpState

@Composable
fun AccountImage(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    viewModel: AccountImageViewModel = hiltViewModelScoped(),
) {

    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        val state = viewModel.pfpState

        when (state) {
            // todo: the alternative is having one Image with painters changed
            // based on whther there is a URL or not

            is PfpState.Loaded -> {
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape),
                    model = state.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = rememberVectorPainter(Icons.Filled.AccountCircle), // todo: is rememberVectorPainter a good idea?
                )
            }

            is PfpState.NotLoaded -> {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                )
            }
        }
    }
}