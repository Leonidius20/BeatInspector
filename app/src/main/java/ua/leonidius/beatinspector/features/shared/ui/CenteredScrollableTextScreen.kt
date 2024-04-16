package ua.leonidius.beatinspector.features.shared.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.leonidius.beatinspector.ui.theme.Dimens

/**
 * A screen that centers its content, limits its width and makes it scrollable.
 * Perfect for making sure that a long text is comfortable to read on a wide screen
 * @param content The content of the screen
 * @param modifier The modifier for the vertical-scrollable column
 */
@Composable
fun CenteredScrollableTextScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .fillMaxHeight()
            .padding(Dimens.paddingNormal)
            //.widthIn(min = 0.dp, max = 250.dp),
    ) {
        BoxWithConstraints(
            Modifier.align(Alignment.CenterHorizontally)
        ) {
            val boxScope = this

            val width = if (boxScope.maxWidth < 500.dp) boxScope.maxWidth else 500.dp

            Column(Modifier.width(width)) {
                content()
            }
        }
    }
}