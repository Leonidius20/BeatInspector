package ua.leonidius.beatinspector.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import ua.leonidius.beatinspector.R

object Dimens {

    val paddingNormal: Dp
        @Composable get() = dimensionResource(id = R.dimen.padding_normal)

    val paddingSmall: Dp
        @Composable get() = dimensionResource(id = R.dimen.padding_small)

}