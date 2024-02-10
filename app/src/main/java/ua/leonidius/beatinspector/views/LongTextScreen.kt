package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ireward.htmlcompose.HtmlText
import ua.leonidius.beatinspector.Dimens
import ua.leonidius.beatinspector.viewmodels.LongTextViewModel

@Composable
fun LongTextScreen(
    modifier: Modifier = Modifier,
    viewModel: LongTextViewModel = viewModel(factory = LongTextViewModel.Factory),
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

    Column(modifier.verticalScroll(rememberScrollState())) {
        BoxWithConstraints(
            Modifier.align(Alignment.CenterHorizontally)
        ) {
            val boxScope = this

            val width = if (boxScope.maxWidth < 500.dp) boxScope.maxWidth else 500.dp

            SelectionContainer {
                HtmlText(
                    modifier = modifier
                        .padding(Dimens.paddingNormal)
                        .width(width),
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Justify),
                )
            }
        }
    }

}