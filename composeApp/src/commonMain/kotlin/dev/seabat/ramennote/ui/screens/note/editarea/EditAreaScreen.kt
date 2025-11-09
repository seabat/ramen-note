package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.components.AppAlert
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.AppTwoButtonAlert
import dev.seabat.ramennote.ui.components.MaxWidthButton
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.editarea_change_image_button
import ramennote.composeapp.generated.resources.editarea_delete_button
import ramennote.composeapp.generated.resources.editarea_delete_confirm
import ramennote.composeapp.generated.resources.editarea_edit_button
import ramennote.composeapp.generated.resources.editarea_image_load_error
import ramennote.composeapp.generated.resources.editarea_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAreaScreen(
    areaName: String,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: EditAreaViewModelContract = koinViewModel<EditAreaViewModel>()
) {
    LaunchedEffect(Unit) {
        viewModel.currentAreaName = areaName
        viewModel.loadImage(areaName)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        var areaName by remember { mutableStateOf(areaName) }
        var shouldShowAlert by remember { mutableStateOf(false) }
        val deleteStatus by viewModel.deleteState.collectAsState()
        val editStatus by viewModel.editState.collectAsState()

        Column(modifier = Modifier.fillMaxSize()) {
            val imageState by viewModel.imageState.collectAsState()
            val focusManager = LocalFocusManager.current

            AppBar(
                title = stringResource(Res.string.editarea_title),
                onBackClick = onBackClick
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier =
                        Modifier
                            .pointerInput(Unit) {
                                detectTapGestures { focusManager.clearFocus() }
                            }
                ) {
                    Text(text = "エリア", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = areaName,
                        onValueChange = { areaName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                errorBorderColor = MaterialTheme.colorScheme.error
                            ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions =
                            KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.fetchImage(areaName) },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                    ) {
                        Text(
                            stringResource(Res.string.editarea_change_image_button),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    when (imageState) {
                        is RunStatus.Success -> {
                            // Coilを使用して画像を表示
                            AsyncImage(
                                model = imageState.data,
                                contentDescription = null,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                            )
                        }
                        is RunStatus.Loading -> {
                            AppProgressBar()
                        }
                        is RunStatus.Error -> {
                            AppAlert(
                                message = stringResource(Res.string.editarea_image_load_error) + imageState.message,
                                onConfirm = { onCompleted() }
                            )
                        }
                        is RunStatus.Idle -> {
                            // Do nothing
                        }
                    }
                }

                Spacer(Modifier.weight(1.0f))

                BottomButtons(
                    areaName = areaName,
                    onEditButtonClick = {
                        viewModel.editArea(areaName)
                    },
                    onDeleteButtonClick = {
                        shouldShowAlert = true
                    }
                )
            }
        }

        if (shouldShowAlert) {
            AppTwoButtonAlert(
                message = stringResource(Res.string.editarea_delete_confirm, areaName),
                onConfirm = {
                    viewModel.deleteArea(areaName)
                    shouldShowAlert = false
                },
                onNegative = {
                    shouldShowAlert = false
                }
            )
        }
        EditStatus(editStatus) { onCompleted() }
        DeleteStatus(deleteStatus) { onCompleted() }
    }
}

@Composable
fun BottomButtons(
    areaName: String,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit
) {
    Column {
        MaxWidthButton(
            text = stringResource(Res.string.editarea_edit_button),
            enabled = areaName.isNotBlank()
        ) {
            onEditButtonClick()
        }
        MaxWidthButton(
            text = stringResource(Res.string.editarea_delete_button),
            enabled = areaName.isNotBlank()
        ) {
            onDeleteButtonClick()
        }
    }
}

@Composable
fun EditStatus(
    deleteStatus: RunStatus<String>,
    onCompleted: () -> Unit
) {
    when (deleteStatus) {
        is RunStatus.Success -> {
            onCompleted()
        }
        is RunStatus.Error -> {
            AppAlert(
                message = "${deleteStatus.message}",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Composable
fun DeleteStatus(
    deleteStatus: RunStatus<String>,
    onCompleted: () -> Unit
) {
    when (deleteStatus) {
        is RunStatus.Success -> {
            onCompleted()
        }
        is RunStatus.Error -> {
            AppAlert(
                message = "${deleteStatus.message}",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@Preview
@Composable
fun EditAreaScreen() {
    RamenNoteTheme {
        EditAreaScreen(
            areaName = "エリア名",
            onBackClick = {},
            onCompleted = {},
            viewModel = MockEditAreaViewModel()
        )
    }
}
