package dev.seabat.ramennote.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.seabat.ramennote.data.datasource.AppVersionDataSource
import dev.seabat.ramennote.ui.components.AppBar
import org.jetbrains.compose.resources.stringResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.settings_title

@Composable
fun SettingsScreen() {
    val appVersionDataSource = remember { AppVersionDataSource() }
    val versionName = remember { appVersionDataSource.getVersionName() }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        AppBar(
            title = stringResource(Res.string.settings_title)
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "バージョン",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = versionName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            uriHandler.openUri("https://seabat.notion.site/Ramen-Note-28f13cfcd0e780eca35ff6bd93873457")
                        },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "プライバシーポリシー",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        }
    }
}
