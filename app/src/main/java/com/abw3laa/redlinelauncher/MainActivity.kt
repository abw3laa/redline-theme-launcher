package com.abw3laa.redlinelauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TvTheme(val id: String, val name: String, val description: String, val bg: Color, val panel: Color, val accent: Color, val fg: Color)

private val themes = listOf(
    TvTheme("redline", "Redline Premium", "أزرق فاخر", Color(0xFF07111F), Color(0xFF14243A), Color(0xFF328BFF), Color.White),
    TvTheme("cinema", "Apple Cinema", "سينمائي هادئ", Color(0xFF09090B), Color(0xFF242428), Color(0xFFE5E5EA), Color.White),
    TvTheme("google", "Google TV", "ألوان حيوية", Color(0xFF111722), Color(0xFF263246), Color(0xFF61B8FF), Color.White),
    TvTheme("neon", "Neon Gaming", "نيون بنفسجي", Color(0xFF10091D), Color(0xFF28163B), Color(0xFFBD79FF), Color.White),
    TvTheme("minimal", "Minimal", "بسيط وخفيف", Color(0xFF111111), Color(0xFF252525), Color(0xFF9DDBB3), Color.White),
    TvTheme("classic", "Classic TV", "تلفاز كلاسيكي", Color(0xFF081727), Color(0xFF16324A), Color(0xFFFFB84D), Color.White)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("launcher", MODE_PRIVATE)
        setContent {
            var selectedId by remember { mutableStateOf(prefs.getString("theme", "redline") ?: "redline") }
            var themePicker by remember { mutableStateOf(false) }
            var showAllApps by remember { mutableStateOf(false) }
            var refreshApps by remember { mutableIntStateOf(0) }
            val t = themes.firstOrNull { it.id == selectedId } ?: themes.first()
            val installedApps = remember(refreshApps) { loadLaunchableApps() }
            val favorites = installedApps.take(12)

            MaterialTheme(colorScheme = darkColorScheme(primary = t.accent, background = t.bg, surface = t.panel, onBackground = t.fg, onSurface = t.fg)) {
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(t.bg, t.panel)))) {
                    Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(Modifier.width(170.dp).fillMaxHeight().clip(RoundedCornerShape(22.dp)).background(Color.Black.copy(alpha = .25f)).padding(16.dp)) {
                            Text("R", color = t.accent, fontSize = 42.sp, fontWeight = FontWeight.Black)
                            Text("REDLINE", color = t.fg, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(24.dp))
                            FocusButton("الرئيسية", t, { showAllApps = false })
                            FocusButton("كل التطبيقات", t, { showAllApps = true })
                            FocusButton("تحديث القائمة", t, { refreshApps++ })
                            FocusButton("الثيمات", t, { themePicker = true })
                            Spacer(Modifier.weight(1f))
                            Text("تحكم بالريموت", color = t.fg.copy(alpha = .65f), fontSize = 12.sp)
                        }
                        Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Text("REDLINE THEME LAUNCHER  •  SMART TV", color = t.fg.copy(alpha = .7f), fontSize = 13.sp)
                            }
                            if (showAllApps) {
                                Text("كل التطبيقات", color = t.fg, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                Text("التطبيقات التي يدعم النظام تشغيلها تظهر هنا.", color = t.fg.copy(alpha = .7f), fontSize = 14.sp)
                                if (installedApps.isEmpty()) {
                                    Text("لم يتم العثور على تطبيقات قابلة للتشغيل.", color = t.fg)
                                } else {
                                    installedApps.chunked(4).forEach { rowApps ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                            rowApps.forEach { app ->
                                                AppTile(app, t, Modifier.weight(1f).height(88.dp)) { launchApp(app.activityInfo.packageName) }
                                            }
                                            repeat(4 - rowApps.size) { Spacer(Modifier.weight(1f)) }
                                        }
                                    }
                                }
                            } else {
                                Column(Modifier.fillMaxWidth().height(175.dp).clip(RoundedCornerShape(24.dp))
                                    .background(Brush.linearGradient(listOf(t.panel, t.accent.copy(alpha = .25f), t.bg))).padding(24.dp),
                                    verticalArrangement = Arrangement.Center) {
                                    Text("عالم الترفيه بين يديك", color = t.fg, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.height(8.dp))
                                    Text("تطبيقاتك المثبتة في مكان واحد — اختر مظهرك المفضل", color = t.fg.copy(alpha = .7f), fontSize = 15.sp)
                                    Spacer(Modifier.height(14.dp))
                                    FocusButton("تغيير الثيم", t, { themePicker = true })
                                }
                                Text("التطبيقات المفضلة", color = t.fg, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                if (favorites.isEmpty()) {
                                    Text("لم يتم العثور على تطبيقات قابلة للتشغيل.", color = t.fg)
                                } else {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(favorites, key = { it.activityInfo.packageName }) { app ->
                                            AppTile(app, t, Modifier.width(145.dp).height(94.dp)) { launchApp(app.activityInfo.packageName) }
                                        }
                                    }
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    FocusButton("عرض كل التطبيقات", t, { showAllApps = true }, Modifier.weight(1f))
                                    FocusButton("إعادة تحميل التطبيقات", t, { refreshApps++ }, Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    if (themePicker) AlertDialog(
                        onDismissRequest = { themePicker = false },
                        containerColor = t.panel,
                        title = { Text("اختر الثيم", color = t.fg) },
                        text = {
                            Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                themes.forEach { option ->
                                    Tile(option.name + " — " + option.description, option, Modifier.fillMaxWidth().height(58.dp)) {
                                        selectedId = option.id
                                        prefs.edit().putString("theme", option.id).apply()
                                        themePicker = false
                                    }
                                }
                            }
                        },
                        confirmButton = { TextButton(onClick = { themePicker = false }) { Text("إغلاق", color = t.accent) } }
                    )
                }
            }
        }
    }

    private fun loadLaunchableApps(): List<ResolveInfo> {
        val categories = listOf(Intent.CATEGORY_LEANBACK_LAUNCHER, Intent.CATEGORY_LAUNCHER)
        val found = linkedMapOf<String, ResolveInfo>()
        categories.forEach { category ->
            val intent = Intent(Intent.ACTION_MAIN).addCategory(category)
            packageManager.queryIntentActivities(intent, 0).forEach { info ->
                if (info.activityInfo.packageName != packageName) found.putIfAbsent(info.activityInfo.packageName, info)
            }
        }
        return found.values.sortedBy { it.loadLabel(packageManager).toString().lowercase() }
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { startActivity(it) }
    }
}

@Composable
private fun FocusButton(label: String, t: TvTheme, onClick: () -> Unit, modifier: Modifier = Modifier) {
    var focused by remember { mutableStateOf(false) }
    Surface(onClick = onClick, modifier = modifier.onFocusChanged { focused = it.isFocused },
        shape = RoundedCornerShape(12.dp), color = if (focused) t.accent.copy(alpha = .25f) else Color.Transparent,
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) t.accent else Color.Transparent)) {
        Text(label, Modifier.padding(horizontal = 12.dp, vertical = 11.dp), color = t.fg, fontSize = 14.sp)
    }
}

@Composable
private fun AppTile(app: ResolveInfo, t: TvTheme, modifier: Modifier, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val label = app.loadLabel(androidx.compose.ui.platform.LocalContext.current.packageManager).toString()
    Surface(onClick = onClick, modifier = modifier.onFocusChanged { focused = it.isFocused },
        shape = RoundedCornerShape(16.dp), color = if (focused) t.accent.copy(alpha = .3f) else t.panel,
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) t.accent else Color.White.copy(alpha = .1f))) {
        Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
            Text(label, color = t.fg, fontSize = 14.sp, fontWeight = if (focused) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
private fun Tile(label: String, t: TvTheme, modifier: Modifier, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Surface(onClick = onClick, modifier = modifier.onFocusChanged { focused = it.isFocused },
        shape = RoundedCornerShape(16.dp), color = if (focused) t.accent.copy(alpha = .3f) else t.panel,
        border = BorderStroke(if (focused) 2.dp else 1.dp, if (focused) t.accent else Color.White.copy(alpha = .1f))) {
        Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
            Text(label, color = t.fg, fontSize = 14.sp, fontWeight = if (focused) FontWeight.Bold else FontWeight.Medium)
        }
    }
}
