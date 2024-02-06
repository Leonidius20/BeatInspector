package ua.leonidius.beatinspector.views

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ireward.htmlcompose.HtmlText
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
        HtmlText(
            modifier = modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Justify),
        )
    }
}