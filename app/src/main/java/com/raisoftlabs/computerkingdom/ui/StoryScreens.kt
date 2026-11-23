package com.raisoftlabs.computerkingdom.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raisoftlabs.computerkingdom.game.ChapterId
import com.raisoftlabs.computerkingdom.game.GameViewModel
import com.raisoftlabs.computerkingdom.game.Narrator
import com.raisoftlabs.computerkingdom.game.Screen
import com.raisoftlabs.computerkingdom.game.Expression

@Composable
fun DialogueScreen(vm: GameViewModel, story: Screen.Story) {
    val t = vm.text
    var index by remember(story) { mutableIntStateOf(0) }
    val line = story.lines[index.coerceIn(story.lines.indices)]
    LaunchedEffect(index, vm.settings.language) {
        if (vm.settings.narration) Narrator.speak(line.text)
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(line.speaker, { vm.navigate(Screen.Menu) }, t.back)
        Spacer(Modifier.height(10.dp))
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CharacterArt(line.character, line.expression, Modifier.size(170.dp))
                Spacer(Modifier.height(12.dp))
                Text(line.speaker, fontSize = scaledSp(18f), fontWeight = FontWeight.Bold, color = Gold)
                Spacer(Modifier.height(8.dp))
                Text(line.text, fontSize = scaledSp(17f), color = Cream, textAlign = TextAlign.Center)
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    story.lines.indices.forEach { dot ->
                        Text(if (dot == index) "●" else "○", color = if (dot == index) Cyan else Muted, fontSize = scaledSp(12f))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        KingdomButton(t.continueButton, {
            vm.playTap()
            if (index == story.lines.lastIndex) vm.navigate(story.next) else index++
        }, color = Indigo)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun LessonScreen(vm: GameViewModel, chapter: ChapterId) {
    val t = vm.text
    val lesson = vm.lesson(chapter)
    var step by remember(chapter) { mutableIntStateOf(0) }
    val current = lesson.steps[step]
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(lesson.title, { vm.navigate(Screen.Map) }, t.back)
        Text(lesson.subtitle, fontSize = scaledSp(13f), color = Muted, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(10.dp))
        ProgressBar((step + 1).toFloat() / lesson.steps.size, Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(14.dp))
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CharacterArt(current.character, Expression.THINK, Modifier.size(140.dp))
                Spacer(Modifier.height(10.dp))
                Text(t.lessonTime, fontSize = scaledSp(12f), color = Cyan)
                Spacer(Modifier.height(4.dp))
                Text(current.title, fontSize = scaledSp(20f), fontWeight = FontWeight.Bold, color = Cream, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text(current.body, fontSize = scaledSp(16f), color = Cream, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(12.dp))
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(t.challenge, fontSize = scaledSp(14f), fontWeight = FontWeight.Bold, color = Gold)
                Spacer(Modifier.height(4.dp))
                Text(lesson.challenge, fontSize = scaledSp(14f), color = Muted)
            }
        }
        Spacer(Modifier.height(14.dp))
        KingdomButton(
            text = if (step == lesson.steps.lastIndex) t.challenge else t.next,
            onClick = {
                vm.playTap()
                if (step == lesson.steps.lastIndex) vm.navigate(Screen.Game(lesson.game, chapter)) else step++
            },
            color = Cyan,
            contentColor = Ink,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun RewardScreen(vm: GameViewModel, reward: Screen.Reward) {
    val t = vm.text
    val lesson = vm.lesson(reward.chapter)
    val art = when (reward.chapter) {
        ChapterId.KINGDOM -> com.raisoftlabs.computerkingdom.game.CharacterId.BYTE
        ChapterId.CPU -> com.raisoftlabs.computerkingdom.game.CharacterId.CORE
        ChapterId.RAM -> com.raisoftlabs.computerkingdom.game.CharacterId.RAMI
        ChapterId.SSD -> com.raisoftlabs.computerkingdom.game.CharacterId.STELLA
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ScreenTopBar(t.chapterComplete, { vm.navigate(Screen.Map) }, t.back)
        Spacer(Modifier.height(8.dp))
        PanelCard(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                CharacterArt(art, modifier = Modifier.size(160.dp))
                Spacer(Modifier.height(10.dp))
                Text(t.wellDone, fontSize = scaledSp(25f), fontWeight = FontWeight.ExtraBold, color = Gold)
                Spacer(Modifier.height(4.dp))
                Text(lesson.title, fontSize = scaledSp(17f), color = Cream, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                StarRating(reward.stars)
                Spacer(Modifier.height(6.dp))
                Text("${t.score}: ${reward.stars}", fontSize = scaledSp(14f), color = Muted)
                Spacer(Modifier.height(16.dp))
                Text(t.newCard, fontSize = scaledSp(13f), color = Cyan)
                Text(lesson.cardName, fontSize = scaledSp(18f), fontWeight = FontWeight.Bold, color = Cream, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Text(t.takeaway, fontSize = scaledSp(13f), color = Muted)
                Text(lesson.takeaway, fontSize = scaledSp(15f), color = Cream, textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(16.dp))
        KingdomButton(t.backToMap, { vm.navigate(Screen.Map) }, color = Indigo)
        Spacer(Modifier.height(10.dp))
        KingdomButton(t.playAgain, { vm.startChapter(reward.chapter) }, color = PanelLight)
        Spacer(Modifier.height(16.dp))
    }
}
