package com.abw3laa.redlinelauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
    TvTheme("redline","Redline Premium","أزرق فاخر",Color(0xFF07111F),Color(0xFF14243A),Color(0xFF328BFF),Color.White),
    TvTheme("cinema","Apple Cinema","سينمائي هادئ",Color(0xFF09090B),Color(0xFF242428),Color(0xFFE5E5EA),Color.White),
    TvTheme("google","Google TV","ألوان حيوية",Color(0xFF111722),Color(0xFF263246),Color(0xFF61B8FF),Color.White),
    TvTheme("neon","Neon Gaming","نيون بنفسجي",Color(0xFF10091D),Color(0xFF28163B),Color(0xFFBD79FF),Color.White),
    TvTheme("minimal","Minimal","بسيط وخفيف",Color(0xFF111111),Color(0xFF252525),Color(0xFF9DDBB3),Color.White),
    TvTheme("classic","Classic TV","تلفاز كلاسيكي",Color(0xFF081727),Color(0xFF16324A),Color(0xFFFFB84D),Color.White)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("launcher", MODE_PRIVATE)
        setContent {
            var selectedId by remember { mutableStateOf(prefs.getString("theme", "redline") ?: "redline") }
            var themePicker by remember { mutableStateOf(false) }
            val t = themes.firstOrNull { it.id == selectedId } ?: themes.first()
            MaterialTheme(colorScheme = darkColorScheme(primary=t.accent, background=t.bg, surface=t.panel, onBackground=t.fg, onSurface=t.fg)) {
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(t.bg, t.panel)))) {
                    Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement=Arrangement.spacedBy(24.dp)) {
                        Column(Modifier.width(160.dp).fillMaxHeight().clip(RoundedCornerShape(22.dp)).background(Color.Black.copy(alpha=.25f)).padding(16.dp)) {
                            Text("R", color=t.accent, fontSize=42.sp, fontWeight=FontWeight.Black)
                            Text("REDLINE", color=t.fg, fontSize=17.sp, fontWeight=FontWeight.Bold)
                            Spacer(Modifier.height(24.dp))
                            listOf("الرئيسية","التلفزيون","الأفلام","التطبيقات").forEach { label -> FocusButton(label,t,{}) }
                            FocusButton("الثيمات",t,{themePicker=true})
                            Spacer(Modifier.weight(1f))
                            Text("تحكم بالريموت",color=t.fg.copy(alpha=.65f),fontSize=12.sp)
                        }
                        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement=Arrangement.spacedBy(18.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.End) { Text("REDLINE THEME LAUNCHER   •   SMART TV",color=t.fg.copy(alpha=.7f),fontSize=13.sp) }
                            Column(Modifier.fillMaxWidth().height(175.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(t.panel,t.accent.copy(alpha=.25f),t.bg))).padding(24.dp), verticalArrangement=Arrangement.Center) {
                                Text("عالم الترفيه بين يديك",color=t.fg,fontSize=29.sp,fontWeight=FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text("تطبيقاتك في مكان واحد — اختر مظهرك المفضل",color=t.fg.copy(alpha=.7f),fontSize=15.sp)
                                Spacer(Modifier.height(14.dp))
                                FocusButton("تغيير الثيم",t,{themePicker=true})
                            }
                            Text("المفضلة",color=t.fg,fontSize=19.sp,fontWeight=FontWeight.Bold)
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                                items(listOf("IPTV+","YouTube","Netflix","Prime Video","Shahid","الإعدادات")) { app -> Tile(app,t,Modifier.width(130.dp).height(88.dp)) {} }
                            }
                            Text("الأقسام",color=t.fg,fontSize=19.sp,fontWeight=FontWeight.Bold)
                            LazyRow(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                                items(listOf("مباشر","أفلام","مسلسلات","رياضة","أخبار","أطفال")) { item -> Tile(item,t,Modifier.width(115.dp).height(64.dp)) {} }
                            }
                        }
                    }
                    if (themePicker) AlertDialog(
                        onDismissRequest={themePicker=false}, containerColor=t.panel,
                        title={Text("اختر الثيم",color=t.fg)},
                        text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            themes.forEach { option ->
                                Tile(option.name + " — " + option.description, option, Modifier.fillMaxWidth().height(58.dp)) {
                                    selectedId=option.id; prefs.edit().putString("theme",option.id).apply(); themePicker=false
                                }
                            }
                        }},
                        confirmButton={TextButton(onClick={themePicker=false}) { Text("إغلاق",color=t.accent) }}
                    )
                }
            }
        }
    }
}

@Composable private fun FocusButton(label:String,t:TvTheme,onClick:()->Unit) {
    var focused by remember { mutableStateOf(false) }
    Surface(onClick=onClick, modifier=Modifier.fillMaxWidth().onFocusChanged{focused=it.isFocused}, shape=RoundedCornerShape(12.dp),
        color=if(focused) t.accent.copy(alpha=.25f) else Color.Transparent,
        border=BorderStroke(if(focused) 2.dp else 1.dp, if(focused) t.accent else Color.Transparent)) {
        Text(label,Modifier.padding(horizontal=12.dp,vertical=11.dp),color=t.fg,fontSize=14.sp)
    }
}
@Composable private fun Tile(label:String,t:TvTheme,modifier:Modifier,onClick:()->Unit) {
    var focused by remember { mutableStateOf(false) }
    Surface(onClick=onClick,modifier=modifier.onFocusChanged{focused=it.isFocused},shape=RoundedCornerShape(16.dp),
        color=if(focused) t.accent.copy(alpha=.3f) else t.panel,
        border=BorderStroke(if(focused) 2.dp else 1.dp,if(focused) t.accent else Color.White.copy(alpha=.1f))) {
        Box(Modifier.fillMaxSize().padding(10.dp),contentAlignment=Alignment.Center) {
            Text(label,color=t.fg,fontSize=14.sp,fontWeight=if(focused) FontWeight.Bold else FontWeight.Medium)
        }
    }
}