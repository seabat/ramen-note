package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.component.AppAlert
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.component.AppProgressBar
import dev.seabat.ramennote.ui.component.MaxWidthButton
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.add_shop_add_button
import ramennote.composeapp.generated.resources.add_shop_area

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAreaScreen(
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: AddAreaViewModelContract = koinViewModel<AddAreaViewModel>()
) {
    var areaName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val addState by viewModel.addState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppBar(
                title = "登録",
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { focusManager.clearFocus() }
                    },
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(Res.string.add_shop_area),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = areaName,
                        onValueChange = { areaName = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.outline,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                }
                MaxWidthButton(
                    text = stringResource(Res.string.add_shop_add_button),
                    enabled = areaName.isNotBlank()
                ) {
                    if (areaName.isNotBlank()) {
                        viewModel.addArea(areaName)
                    }
                }
            }
        }

        AddStatus(addState) { onCompleted() }
    }
}

@Composable
fun AddStatus(
    addStatus: RunStatus<ByteArray?>,
    onCompleted: () -> Unit
){
    when (addStatus) {
        is RunStatus.Success -> { onCompleted() }
        is RunStatus.Error -> {
            AppAlert(
                message = "${addStatus.message}",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */}
    }
}

@Preview
@Composable
fun AddAreaScreenView() {
    RamenNoteTheme {
        AddAreaScreen(
            onBackClick = {},
            onCompleted = {},
            viewModel = MockAddAreaViewModel()
        )
    }
}
