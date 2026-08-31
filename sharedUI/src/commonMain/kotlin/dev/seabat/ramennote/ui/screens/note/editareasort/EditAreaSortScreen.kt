package dev.seabat.ramennote.ui.screens.note.editareasort

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.components.AppBar
import dev.seabat.ramennote.ui.components.AppProgressBar
import dev.seabat.ramennote.ui.components.alert.AppAlert
import dev.seabat.ramennote.ui.components.button.MaxWidthButton
import dev.seabat.ramennote.ui.navigation.Screen
import dev.seabat.ramennote.ui.theme.RamenNoteTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import ramennote.sharedui.generated.resources.Res
import ramennote.sharedui.generated.resources.add_circle_24px
import ramennote.sharedui.generated.resources.do_not_disturb_on_24px
import ramennote.sharedui.generated.resources.editarea_edit_button
import ramennote.sharedui.generated.resources.editareasort_description
import ramennote.sharedui.generated.resources.editareasort_title

@NavDestination(route = Screen.EditAreaSort::class)
@Composable
fun EditAreaSortScreen(
    onBackClick: () -> Unit,
    onCompleted: () -> Unit,
    viewModel: EditAreaSortViewModelContract = koinViewModel<EditAreaSortViewModel>()
) {
    val areas by viewModel.areas.collectAsState()
    val editAreasState by viewModel.editAreasState.collectAsState()

    // 画面表示時にデータを取得
    LaunchedEffect(Unit) {
        viewModel.loadAreas()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title = stringResource(Res.string.editareasort_title),
                onBackClick = onBackClick
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.editareasort_description)
                )
                AreaListMainContent(
                    areas,
                    onValueChange = { areaName, sort -> viewModel.setSort(areaName, sort) },
                    onEditButtonClick = { viewModel.editAreaSort() }
                )
            }
        }
    }

    EditAreasStatus(
        status = editAreasState,
        onCompleted = onCompleted
    )
}

@Composable
private fun AreaListMainContent(
    areas: List<Area>,
    onValueChange: (areaName: String, sort: Int) -> Unit,
    onEditButtonClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp) // FAB と重ならない余白
    ) {
        item {
            HorizontalDivider(
                Modifier,
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
        items(areas) { area ->
            AreaItem(
                area = area,
                onValueChange = { sort -> onValueChange(area.name, if (sort.isBlank()) 0 else sort.toIntOrNull() ?: 0) }
            )
        }
        item {
            MaxWidthButton(
                text = stringResource(Res.string.editarea_edit_button)
            ) {
                onEditButtonClick()
            }
        }
    }
}

@Composable
private fun AreaItem(
    area: Area,
    onValueChange: (sort: String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.do_not_disturb_on_24px),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(30.dp)
                        .clickable {
                            val newSort = (area.sort - 1).coerceAtLeast(0)
                            onValueChange(newSort.toString())
                        },
                tint = MaterialTheme.colorScheme.tertiaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier =
                    Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp)
                        )
            ) {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = if (area.sort == 0) "" else area.sort.toString(),
                        onValueChange = onValueChange,
                        modifier =
                            Modifier
                                .width(50.dp)
//                            .heightIn(min = 48.dp)
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                        singleLine = true,
                        textStyle =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right
                            ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = vectorResource(Res.drawable.add_circle_24px),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(30.dp)
                        .clickable {
                            onValueChange((area.sort + 1).toString())
                        },
                tint = MaterialTheme.colorScheme.tertiaryContainer
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = area.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun EditAreasStatus(
    status: RunStatus<String>,
    onCompleted: () -> Unit
) {
    when (status) {
        is RunStatus.Success -> {
            onCompleted()
        }
        is RunStatus.Error -> {
            AppAlert(
                message = status.message ?: "エリアの更新に失敗しました",
                onConfirm = { onCompleted() }
            )
        }
        is RunStatus.Loading -> {
            AppProgressBar()
        }
        is RunStatus.Idle -> { /* Do nothing */ }
    }
}

@NavPreview(Screen.EditAreaSort::class, primary = true)
@Preview
@Composable
fun EditAreaSortScreenPreview() {
    RamenNoteTheme {
        EditAreaSortScreen(
            onBackClick = {},
            onCompleted = {},
            viewModel = MockEditAreaSortViewModel()
        )
    }
}

@Preview
@Composable
private fun AreaItemPreview() {
    RamenNoteTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AreaItem(
                area =
                    Area(
                        name = "東京",
                        updatedDate = LocalDate(2024, 9, 1),
                        count = 12,
                        sort = 3
                    ),
                onValueChange = {}
            )
            AreaItem(
                area =
                    Area(
                        name = "神奈川",
                        updatedDate = LocalDate(2024, 8, 21),
                        count = 5,
                        sort = 2
                    ),
                onValueChange = {}
            )
            AreaItem(
                area =
                    Area(
                        name = "徳島",
                        updatedDate = LocalDate(2024, 7, 3),
                        count = 2,
                        sort = 1
                    ),
                onValueChange = {}
            )
        }
    }
}
