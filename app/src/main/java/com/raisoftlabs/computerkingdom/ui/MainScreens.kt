package com.raisoftlabs.computerkingdom.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raisoftlabs.computerkingdom.game.CharacterId
import com.raisoftlabs.computerkingdom.game.ChapterId
import com.raisoftlabs.computerkingdom.game.ComponentInfo
import com.raisoftlabs.computerkingdom.game.GameRules
import com.raisoftlabs.computerkingdom.game.GameViewModel
import com.raisoftlabs.computerkingdom.game.Language
import com.raisoftlabs.computerkingdom.game.LocationId
import com.raisoftlabs.computerkingdom.game.Screen
import com.raisoftlabs.computerkingdom.game.locationName
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(title: String, tagline: String, onDone: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "splash")
    val drift by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "drift")
    LaunchedEffect(Unit) {
        delay(2100)
        onDone()
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        CharacterArt(CharacterId.BYTE, modifier = Modifier.size(160.dp).offset(y = ((-6f - drift * 10f)).dp))
        Spacer(Modifier.height(18.dp))
        Text(title, fontSize = scaledSp(32f), fontWeight = FontWeight.ExtraBold, color = Cream, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(tagline, fontSize = scaledSp(15f), color = Muted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(34.dp))
        CircularProgressIndicator(color = Cyan, modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
    }
}

@Composable
fun MainMenuScreen(vm: GameViewModel) {
    val t = vm.text
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CharacterArt(CharacterId.BYTE, modifier = Modifier.size(86.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.appTitle, fontSize = scaledSp(28f), fontWeight = FontWeight.ExtraBold, color = Cream)
                Text(t.tagline, fontSize = scaledSp(14f), color = Muted)
            }
        }
        PanelCard {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                StarRating(vm.progress.totalStars().coerceAtMost(3))
                Spacer(Modifier.width(12.dp))
                Text("${vm.progress.totalStars()} ${t.stars.lowercase()}", fontSize = scaledSp(15f), fontWeight = FontWeight.Bold, color = Gold)
            }
        }
        KingdomButton(t.playAdventure, { vm.navigate(Screen.Map) }, icon = Icons.Filled.PlayArrow, color = Indigo)
        KingdomButton(t.exploreComputer, { vm.navigate(Screen.Explore) }, icon = Icons.Filled.Explore, color = Cyan, contentColor = Ink)
        KingdomButton(t.myCollection, { vm.navigate(Screen.Collection) }, icon = Icons.Filled.Collections, color = Lavender, contentColor = Ink)
        KingdomButton(t.parentArea, { vm.newGateQuestion(); vm.navigate(Screen.ParentGate) }, icon = Icons.Filled.FamilyRestroom, color = PanelLight)
        KingdomButton(t.settings, { vm.navigate(Screen.Settings) }, icon = Icons.Filled.Settings, color = PanelLight)
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun WorldMapScreen(vm: GameViewModel) {
    val t = vm.text
    val locations = LocationId.entries
    val points = listOf(
        0.20f to 0.14f, 0.68f to 0.19f, 0.32f to 0.37f,
        0.76f to 0.45f, 0.27f to 0.62f, 0.70f to 0.70f, 0.47f to 0.86f,
    )
    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        ScreenTopBar(t.mapTitle, { vm.navigate(Screen.Menu) }, t.back)
        Text(t.mapSubtitle, fontSize = scaledSp(14f), color = Muted, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        BoxWithConstraints(Modifier.fillMaxSize().weight(1f)) {
            val width = maxWidth
            val height = maxHeight
            Canvas(Modifier.fillMaxSize()) {
                val px = points.map { Offset(it.first * width.toPx(), it.second * height.toPx()) }
                for (index in 0 until px.size - 1) {
                    val start = px[index]
                    val end = px[index + 1]
                    val middle = Offset((start.x + end.x) / 2f, (start.y + end.y) / 2f + 24.dp.toPx())
                    val path = Path().apply {
                        moveTo(start.x, start.y)
                        quadraticBezierTo(middle.x, middle.y, end.x, end.y)
                    }
                    drawPath(path, color = Color(0xFF3C4C86), style = androidx.compose.ui.graphics.drawscope.Stroke(5.dp.toPx()))
                }
            }
            locations.forEachIndexed { index, location ->
                val chapter = GameRules.chapterForLocation(location)
                val unlocked = chapter?.let { GameRules.isUnlocked(vm.progress.completed, it) } ?: false
                val starCount = chapter?.let { vm.progress.stars[it] ?: 0 } ?: 0
                val point = points[index]
                val art = when (location) {
                    LocationId.POWER -> CharacterId.VOLT
                    LocationId.CONNECTION -> CharacterId.MOTHERBOARD
                    LocationId.PROCESSOR -> CharacterId.CORE
                    LocationId.MEMORY -> CharacterId.RAMI
                    LocationId.STORAGE -> CharacterId.STELLA
                    LocationId.GRAPHICS -> CharacterId.PIXEL
                    LocationId.HALL -> CharacterId.BYTE
                }
                Column(
                    Modifier
                        .offset(x = width * point.first - 48.dp, y = height * point.second - 52.dp)
                        .width(96.dp)
                        .clickable {
                            if (chapter != null && unlocked) vm.startChapter(chapter) else vm.showMessage(t.comingSoonBody)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(Modifier.size(70.dp), contentAlignment = Alignment.Center) {
                        CharacterArt(art, modifier = Modifier.size(70.dp).alpha(if (unlocked) 1f else 0.35f))
                        if (!unlocked) {
                            Icon(Icons.Filled.Lock, contentDescription = t.locked, tint = Cream, modifier = Modifier.size(22.dp))
                        }
                    }
                    Text(locationName(vm.settings.language, location), fontSize = scaledSp(12f), color = if (unlocked) Cream else Muted, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                    if (starCount > 0) StarRating(starCount, Modifier.height(18.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        val kingdomDone = ChapterId.KINGDOM in vm.progress.completed
        KingdomButton(
            text = if (kingdomDone) "${t.kingdomGate} · ${t.playAgain}" else t.kingdomGate,
            onClick = { vm.startChapter(ChapterId.KINGDOM) },
            color = Gold,
            contentColor = Ink,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun ExploreScreen(vm: GameViewModel) {
    val t = vm.text
    var selected by remember { mutableStateOf<ComponentInfo?>(null) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(t.exploreTitle, { vm.navigate(Screen.Menu) }, t.back)
        Text(t.exploreSubtitle, fontSize = scaledSp(14f), color = Muted, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(12.dp))
        val components = vm.componentText.items
        for (row in components.chunked(2)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    PanelCard(Modifier.weight(1f).clickable { selected = item; vm.playTap() }) {
                        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            CharacterArt(item.character, modifier = Modifier.size(70.dp))
                            Spacer(Modifier.height(6.dp))
                            Text(item.name, fontSize = scaledSp(14f), fontWeight = FontWeight.Bold, color = Cream, textAlign = TextAlign.Center)
                            Text(item.short, fontSize = scaledSp(12f), color = Cyan, textAlign = TextAlign.Center)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                if (selected == null) {
                    Text(t.tapToLearn, fontSize = scaledSp(17f), fontWeight = FontWeight.Bold, color = Cream)
                } else {
                    Text(selected!!.name, fontSize = scaledSp(19f), fontWeight = FontWeight.Bold, color = Gold)
                    Spacer(Modifier.height(6.dp))
                    Text(selected!!.description, fontSize = scaledSp(15f), color = Cream)
                }
                Spacer(Modifier.height(12.dp))
                KingdomButton(t.meetByte, {
                    vm.navigate(Screen.Story(vm.lesson(ChapterId.KINGDOM).intro, null, Screen.Explore))
                }, color = Indigo)
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
fun CollectionScreen(vm: GameViewModel) {
    val t = vm.text
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(t.collectionTitle, { vm.navigate(Screen.Menu) }, t.back)
        val cards = vm.cardList
        if (vm.progress.cards.isEmpty() && vm.progress.badges.isEmpty()) {
            Text(t.collectionEmpty, fontSize = scaledSp(15f), color = Muted, modifier = Modifier.padding(16.dp))
        }
        for (row in cards.chunked(2)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { card ->
                    val earned = card.id in vm.progress.cards || card.id in vm.progress.badges
                    PanelCard(Modifier.weight(1f)) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            CharacterArt(card.character, modifier = Modifier.size(66.dp).alpha(if (earned) 1f else 0.22f))
                            Spacer(Modifier.height(6.dp))
                            Text(if (earned) card.name else t.locked, fontSize = scaledSp(13f), fontWeight = FontWeight.Bold, color = if (earned) Cream else Muted, textAlign = TextAlign.Center)
                            if (!earned) Text(card.requirement, fontSize = scaledSp(11f), color = Muted, textAlign = TextAlign.Center)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun ParentGateScreen(vm: GameViewModel) {
    val t = vm.text
    var answer by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
        ScreenTopBar(t.parentGateTitle, { vm.navigate(Screen.Menu) }, t.back)
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(t.parentGateTitle, fontSize = scaledSp(21f), fontWeight = FontWeight.Bold, color = Cream)
                Spacer(Modifier.height(10.dp))
                Text(t.parentGatePrompt, fontSize = scaledSp(15f), color = Muted, textAlign = TextAlign.Center)
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = answer,
                    onValueChange = { answer = it },
                    singleLine = true,
                    label = { Text("?") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(14.dp))
                KingdomButton(t.continueButton, {
                    if (answer.trim().toIntOrNull() == vm.gateAnswer) vm.navigate(Screen.Parent) else vm.showMessage(t.wrongAnswer)
                }, color = Indigo)
                if (vm.toast != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(vm.toast.orEmpty(), color = Coral, fontSize = scaledSp(13f), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun ParentDashboardScreen(vm: GameViewModel) {
    val t = vm.text
    var confirmReset by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(t.parentTitle, { vm.navigate(Screen.Menu) }, t.back)
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text(t.completedChapters, fontSize = scaledSp(17f), fontWeight = FontWeight.Bold, color = Gold)
                Spacer(Modifier.height(8.dp))
                val lessons = vm.lessonList
                lessons.forEach { lesson ->
                    val done = lesson.chapter in vm.progress.completed
                    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (done) "✓" else "○", color = if (done) Mint else Muted, fontSize = scaledSp(16f))
                        Spacer(Modifier.width(10.dp))
                        Text(lesson.title, Modifier.weight(1f), fontSize = scaledSp(14f), color = if (done) Cream else Muted)
                        if (done) StarRating(vm.progress.stars[lesson.chapter] ?: 0)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(t.conceptsLearned, fontSize = scaledSp(17f), fontWeight = FontWeight.Bold, color = Cyan)
                val learned = lessons.filter { it.chapter in vm.progress.completed }
                if (learned.isEmpty()) {
                    Text(t.needPractice, fontSize = scaledSp(13f), color = Muted, modifier = Modifier.padding(top = 6.dp))
                } else {
                    learned.forEach { lesson -> Text("• ${lesson.takeaway}", fontSize = scaledSp(13f), color = Cream, modifier = Modifier.padding(top = 4.dp)) }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        SettingsControls(vm)
        Spacer(Modifier.height(12.dp))
        KingdomButton(t.resetProgress, { confirmReset = true }, color = Coral, contentColor = Ink)
        Spacer(Modifier.height(20.dp))
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(t.resetProgress) },
            text = { Text(t.resetConfirm) },
            confirmButton = { TextButton(onClick = { vm.resetProgress(); confirmReset = false; vm.showMessage(t.resetDone) }) { Text(t.reset) } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text(t.cancel) } },
        )
    }
}

@Composable
fun SettingsScreen(vm: GameViewModel) {
    val t = vm.text
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(t.settingsTitle, { vm.navigate(Screen.Menu) }, t.back)
        SettingsControls(vm)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SettingsControls(vm: GameViewModel) {
    val t = vm.text
    PanelCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(t.language, fontWeight = FontWeight.Bold, fontSize = scaledSp(16f), color = Gold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceButton(t.english, vm.settings.language == Language.ENGLISH) { vm.setLanguage(Language.ENGLISH) }
                ChoiceButton(t.bangla, vm.settings.language == Language.BANGLA) { vm.setLanguage(Language.BANGLA) }
            }
            Text(t.textSize, fontWeight = FontWeight.Bold, fontSize = scaledSp(16f), color = Cyan)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceButton(t.small, vm.settings.textScale == 0.9f) { vm.setTextScale(0.9f) }
                ChoiceButton(t.medium, vm.settings.textScale == 1f) { vm.setTextScale(1f) }
                ChoiceButton(t.large, vm.settings.textScale == 1.15f) { vm.setTextScale(1.15f) }
            }
            ToggleRow(t.narration, t.narrationBody, vm.settings.narration) { vm.setNarration(it) }
            ToggleRow(t.soundEffects, "", vm.settings.sound) { vm.setSound(it) }
            Text(t.playTime, fontWeight = FontWeight.Bold, fontSize = scaledSp(16f), color = Lavender)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceButton(t.unlimited, vm.settings.playLimitMinutes == 0) { vm.setPlayLimit(0) }
                ChoiceButton(t.minutes15, vm.settings.playLimitMinutes == 15) { vm.setPlayLimit(15) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChoiceButton(t.minutes30, vm.settings.playLimitMinutes == 30) { vm.setPlayLimit(30) }
                ChoiceButton(t.minutes60, vm.settings.playLimitMinutes == 60) { vm.setPlayLimit(60) }
            }
        }
    }
}

@Composable
private fun ChoiceButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Indigo else PanelLight,
            contentColor = if (selected) Cream else Muted,
        ),
        modifier = Modifier.height(44.dp),
    ) {
        Text(label, fontSize = scaledSp(13f), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ToggleRow(label: String, body: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, fontSize = scaledSp(15f), fontWeight = FontWeight.Bold, color = Cream)
            if (body.isNotBlank()) Text(body, fontSize = scaledSp(12f), color = Muted)
        }
        Switch(checked = checked, onCheckedChange = onChange, colors = SwitchDefaults.colors(checkedThumbColor = Cyan, checkedTrackColor = Indigo))
    }
}

@Composable
fun BreakDialog(vm: GameViewModel) {
    val t = vm.text
    AlertDialog(
        onDismissRequest = { vm.dismissBreak() },
        title = { Text(t.breakTitle, fontWeight = FontWeight.Bold) },
        text = { Text(t.breakBody) },
        confirmButton = { TextButton(onClick = { vm.dismissBreak() }) { Text(t.close) } },
    )
}

@Composable
fun ToastMessage(message: String, onDone: () -> Unit) {
    LaunchedEffect(message) {
        delay(2600)
        onDone()
    }
    Box(Modifier.fillMaxSize().padding(bottom = 90.dp), contentAlignment = Alignment.BottomCenter) {
        PanelCard(Modifier.padding(horizontal = 28.dp)) {
            Text(message, modifier = Modifier.padding(16.dp), color = Cream, fontSize = scaledSp(14f), textAlign = TextAlign.Center)
        }
    }
}
