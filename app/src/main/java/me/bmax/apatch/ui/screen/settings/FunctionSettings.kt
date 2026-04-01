package me.bmax.apatch.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.KeyOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.bmax.apatch.APApplication
import me.bmax.apatch.R
import me.bmax.apatch.ui.component.SettingsCategory
import me.bmax.apatch.ui.component.SwitchItem
import me.bmax.apatch.util.setHideServiceEnabled

/**
 * 功能设置页面
 * 包含 FolkPatch Hide 和 Umount Service 功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionSettings(
    searchText: String,
    kPatchReady: Boolean,
    aPatchReady: Boolean,
    isHideServiceEnabled: Boolean,
    onHideServiceChange: (Boolean) -> Unit,
    snackBarHost: SnackbarHostState,
    onNavigateToUmountConfig: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = APApplication.sharedPreferences
    val scope = rememberCoroutineScope()
    
    // Function Category
    val functionTitle = stringResource(R.string.settings_category_function)
    val matchFunction = shouldShow(searchText, functionTitle)
    
    // Hide Service
    val hideServiceTitle = stringResource(id = R.string.settings_hide_service)
    val hideServiceSummary = stringResource(id = R.string.settings_hide_service_summary)
    val showHideService = (kPatchReady && aPatchReady) && (matchFunction || shouldShow(searchText, hideServiceTitle, hideServiceSummary))
    
    // Umount Service
    val umountServiceTitle = stringResource(id = R.string.settings_umount_service)
    val umountServiceSummary = stringResource(id = R.string.settings_umount_service_summary)
    val showUmountService = (kPatchReady && aPatchReady) && (matchFunction || shouldShow(searchText, umountServiceTitle, umountServiceSummary))
    
    val showFunctionCategory = showHideService || showUmountService
    
    if (showFunctionCategory) {
        SettingsCategory(icon = Icons.Filled.Tune, title = functionTitle, isSearching = searchText.isNotEmpty()) {
            // Hide Service - FolkPatch Hide
            if (showHideService) {
                SwitchItem(
                    icon = Icons.Filled.KeyOff,
                    title = hideServiceTitle,
                    summary = hideServiceSummary,
                    checked = isHideServiceEnabled,
                    onCheckedChange = {
                        setHideServiceEnabled(it)
                        onHideServiceChange(it)
                    }
                )
            }
            
            // Umount Service Service
            if (showUmountService) {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    leadingContent = { Icon(Icons.Filled.FolderOff, umountServiceTitle) },
                    supportingContent = { Text(umountServiceSummary) },
                    headlineContent = { Text(umountServiceTitle) },
                    modifier = Modifier.clickable { onNavigateToUmountConfig() }
                )
            }
        }
    }
}

/**
 * 检查搜索文本是否匹配指定内容
 */
private fun shouldShow(searchText: String, vararg targets: String): Boolean {
    if (searchText.isEmpty()) return true
    val searchLower = searchText.lowercase()
    return targets.any { it.lowercase().contains(searchLower) }
}
