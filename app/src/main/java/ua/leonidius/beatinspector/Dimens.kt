package ua.leonidius.beatinspector

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
object Dimens {

    val paddingNormal: Dp
        @Composable get() = dimensionResource(id = R.dimen.padding_normal)

    val paddingSmall: Dp
        @Composable get() = dimensionResource(id = R.dimen.padding_small)

}