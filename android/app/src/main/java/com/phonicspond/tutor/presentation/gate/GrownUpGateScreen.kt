package com.phonicspond.tutor.presentation.gate

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.RewardGold
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextPrimary
import com.phonicspond.tutor.core.theme.TextSecondary
import com.phonicspond.tutor.core.theme.WaterBase
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterLight
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.PondHeader
import com.phonicspond.tutor.core.ui.SoftButton
import com.phonicspond.tutor.core.ui.StatCard
import com.phonicspond.tutor.presentation.dialog.ConfirmResetDialog

@Composable
fun GrownUpGateScreen(
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GateViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    GateContent(
        state = state,
        onHome = onHome,
        onAnswer = viewModel::onAnswer,
        onToggleSoundCues = viewModel::onToggleSoundCues,
        onToggleAutoHints = viewModel::onToggleAutoHints,
        onResetRequested = viewModel::onResetRequested,
        modifier = modifier
    )

    if (state.confirmReset) {
        ConfirmResetDialog(
            onConfirm = viewModel::onResetConfirmed,
            onCancel = viewModel::onResetCancelled
        )
    }
}

@Composable
private fun GateContent(
    state: GateUiState,
    onHome: () -> Unit,
    onAnswer: (Int) -> Unit,
    onToggleSoundCues: (Boolean) -> Unit,
    onToggleAutoHints: (Boolean) -> Unit,
    onResetRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    PondBackground(
        overlay = listOf(
            WaterLight.copy(alpha = 0.94f),
            WaterBase.copy(alpha = 0.92f),
            WaterDeep.copy(alpha = 0.94f)
        ),
        modifier = modifier,
        blobTint = FrogGreen,
        blobStrength = 0.10f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            PondHeader(
                title = stringResource(R.string.grown_ups),
                homeDescription = stringResource(R.string.home_description),
                onHome = onHome
            )
            if (state.unlocked) {
                GatePanel(
                    state = state,
                    onToggleSoundCues = onToggleSoundCues,
                    onToggleAutoHints = onToggleAutoHints,
                    onResetRequested = onResetRequested
                )
            } else {
                GateQuestion(
                    state = state,
                    onAnswer = onAnswer
                )
            }
        }
    }
}

@Composable
private fun GateQuestion(
    state: GateUiState,
    onAnswer: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        ClayCard(
            modifier = Modifier.fillMaxWidth(),
            fill = listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.86f)),
            corner = 30.dp,
            elevation = 14.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.for_grown_ups),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.gate_question),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(22.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.options.forEach { option ->
                        SoftButton(
                            label = stringResource(R.string.number_format, option),
                            onClick = { onAnswer(option) },
                            height = 56.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                if (state.showRetry) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.try_again),
                        style = MaterialTheme.typography.labelLarge,
                        color = LilyPink
                    )
                }
            }
        }
    }
}

@Composable
private fun GatePanel(
    state: GateUiState,
    onToggleSoundCues: (Boolean) -> Unit,
    onToggleAutoHints: (Boolean) -> Unit,
    onResetRequested: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.progress),
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        GateStats(state = state)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.letters),
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        LetterGrid(letters = state.letters, mastered = state.masteredSet)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = stringResource(R.string.sound),
            style = MaterialTheme.typography.labelLarge,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingRow(
            label = stringResource(R.string.setting_sound_cues),
            checked = state.soundCues,
            onCheckedChange = onToggleSoundCues
        )
        Spacer(modifier = Modifier.height(10.dp))
        SettingRow(
            label = stringResource(R.string.setting_auto_hints),
            checked = state.autoHints,
            onCheckedChange = onToggleAutoHints
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = onResetRequested,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.reset_progress),
                style = MaterialTheme.typography.labelLarge,
                color = LilyPink
            )
        }
    }
}

@Composable
private fun GateStats(state: GateUiState) {
    val visible = listOf(
        Triple(stringResource(R.string.letters), state.masteredLetters, FrogGreen),
        Triple(stringResource(R.string.words), state.wordsRead, LilyPink),
        Triple(stringResource(R.string.stickers), state.stickers, RewardGold)
    ).filter { it.second > 0 }

    if (visible.size < 2) {
        Text(
            text = stringResource(R.string.progress_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        visible.forEach { entry ->
            StatCard(
                label = entry.first,
                value = entry.second.toString(),
                accent = entry.third,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LetterGrid(letters: List<Char>, mastered: Set<Char>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        letters.chunked(ROW_LETTERS).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { letter ->
                    LetterChip(
                        letter = letter,
                        mastered = letter in mastered,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(ROW_LETTERS - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LetterChip(
    letter: Char,
    mastered: Boolean,
    modifier: Modifier = Modifier
) {
    val stateLabel = if (mastered) {
        stringResource(R.string.letter_mastered)
    } else {
        stringResource(R.string.letter_learning)
    }
    ClayCard(
        modifier = modifier
            .height(44.dp)
            .semantics { stateDescription = stateLabel },
        fill = if (mastered) {
            listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.86f))
        } else {
            listOf(SurfaceWhite.copy(alpha = 0.58f), SurfaceWhite.copy(alpha = 0.44f))
        },
        corner = 16.dp,
        elevation = if (mastered) 6.dp else 1.dp,
        borderTint = if (mastered) FrogGreen.copy(alpha = 0.45f) else WaterDeep.copy(alpha = 0.5f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (mastered) TextPrimary else TextSecondary.copy(alpha = 0.5f)
            )
            if (mastered) {
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = AppIcons.Check,
                    contentDescription = null,
                    tint = FrogGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val stateLabel = if (checked) {
        stringResource(R.string.setting_on)
    } else {
        stringResource(R.string.setting_off)
    }
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        fill = listOf(SurfaceWhite.copy(alpha = 0.94f), SurfaceWhite.copy(alpha = 0.84f)),
        corner = 22.dp,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = FrogGreen,
                    uncheckedThumbColor = SurfaceWhite,
                    uncheckedTrackColor = WaterDeep
                ),
                modifier = Modifier.semantics { stateDescription = stateLabel }
            )
        }
    }
}

private const val ROW_LETTERS = 7
