package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.component.AppAlert
import dev.seabat.ramennote.ui.component.AppBar
import dev.seabat.ramennote.ui.component.AppProgressBar
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAreaScreen(
    area: String,
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: EditAreaViewModelContract = koinViewModel<EditAreaViewModel>()
) {

    LaunchedEffect(Unit) {
        viewModel.currentAreas = area
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "編集",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            var areaName by remember { mutableStateOf(area) }
            var shouldShowAlert by remember { mutableStateOf(false) }
            val deleteStatus by viewModel.deleteStatus.collectAsState()
            val editStatus by viewModel.editStatus.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    Text(text = "エリア", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = areaName,
                        onValueChange = { areaName = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                BottomContent(
                    areaName = areaName,
                    onEditButtonClick = {
                        viewModel.editArea(areaName)
                    },
                    onDeleteButtonClick = {
                        shouldShowAlert = true
                    },
                )

            }
            if (shouldShowAlert) {
                AppAlert(
                    message = "${areaName}を削除して良いですか？",
                    onConfirm = {
                        viewModel.deleteArea(areaName)
                        shouldShowAlert = false
                    }
                )
            }
            EditStatus(editStatus) { onCompleted() }
            DeleteStatus(deleteStatus) { onCompleted() }
        }
    }
}

@Composable
fun BottomContent(
    areaName: String,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
) {
    Column {
        Button(
            onClick = {
                if (areaName.isNotBlank()) {
                    onEditButtonClick()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "変更する", style = MaterialTheme.typography.titleMedium)
        }

        Button(
            onClick = {
                onDeleteButtonClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = "削除する", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun EditStatus(
    deleteStatus: RunStatus<String>,
    onCompleted: () -> Unit
){
    when (deleteStatus) {
        is RunStatus.Success -> { onCompleted() }
        is RunStatus.Error -> {
            AppAlert(
                message = "${deleteStatus.message}",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */}
    }
}

@Composable
fun DeleteStatus(
    deleteStatus: RunStatus<String>,
    onCompleted: () -> Unit
){
    when (deleteStatus) {
        is RunStatus.Success -> { onCompleted() }
        is RunStatus.Error -> {
            AppAlert(
                message = "${deleteStatus.message}",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> { AppProgressBar() }
        is RunStatus.Idle -> { /* Do nothing */}
    }
}

@Preview
@Composable
fun EditAreaScreen() {
    RamenNoteTheme {
        EditAreaScreen(
            area = "エリア名",
            onBackClick = {},
            onCompleted = {},
            viewModel = MockEditAreaViewModel()
        )
    }
}
