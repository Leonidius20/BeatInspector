package ua.leonidius.beatinspector.features.legal.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ireward.htmlcompose.HtmlText
import ua.leonidius.beatinspector.features.shared.ui.CenteredScrollableTextScreen
import ua.leonidius.beatinspector.features.legal.viewmodels.LongTextViewModel

@Composable
fun LongTextScreen(
    modifier: Modifier = Modifier,
    viewModel: LongTextViewModel = hiltViewModel(),
) {
    LongTextScreen(
        modifier = modifier,
        textId = viewModel.textId
    )
}

@Composable
fun LongTextScreen(
    modifier: Modifier = Modifier,
    @StringRes textId: Int
) {
    LongTextScreen(modifier, text = stringResource(textId))
}

@Composable
fun LongTextScreen(
    modifier: Modifier = Modifier,
    text: String,
) {
    CenteredScrollableTextScreen(modifier = modifier) {
        SelectionContainer {
            HtmlText(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Justify),
            )
        }
    }

}