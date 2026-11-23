package com.raisoftlabs.computerkingdom.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raisoftlabs.computerkingdom.game.ChapterId
import com.raisoftlabs.computerkingdom.game.GameRules
import com.raisoftlabs.computerkingdom.game.GameViewModel
import com.raisoftlabs.computerkingdom.game.Screen
import com.raisoftlabs.computerkingdom.game.SortItem
import kotlinx.coroutines.delay

@Composable
fun MiniGameScreen(vm: GameViewModel, game: Screen.Game) {
    val t = vm.text
    val title = when (game.game) {
        com.raisoftlabs.computerkingdom.game.GameId.FIND_MISSING_PART -> vm.gameText.find.title
        com.raisoftlabs.computerkingdom.game.GameId.INSTRUCTION_ORDER -> vm.gameText.order.title
        com.raisoftlabs.computerkingdom.game.GameId.MEMORY_DESK -> vm.gameText.memory.title
        com.raisoftlabs.computerkingdom.game.GameId.STORAGE_SORT -> vm.gameText.sort.title
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ScreenTopBar(title, { vm.navigate(Screen.Map) }, t.back)
        when (game.game) {
            com.raisoftlabs.computerkingdom.game.GameId.FIND_MISSING_PART -> FindMissingGame(vm, game.chapter)
            com.raisoftlabs.computerkingdom.game.GameId.INSTRUCTION_ORDER -> InstructionOrderGame(vm, game.chapter)
            com.raisoftlabs.computerkingdom.game.GameId.MEMORY_DESK -> MemoryDeskGame(vm, game.chapter)
            com.raisoftlabs.computerkingdom.game.GameId.STORAGE_SORT -> StorageSortGame(vm, game.chapter)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun HintButton(text: String, hint: String, expanded: Boolean, onClick: () -> Unit) {
    Column {
        KingdomButton(text, onClick, color = PanelLight, icon = Icons.Filled.Lightbulb)
        if (expanded) {
            Spacer(Modifier.height(6.dp))
            PanelCard(Modifier.fillMaxWidth()) {
                Text(hint, modifier = Modifier.padding(14.dp), fontSize = scaledSp(13f), color = Gold)
            }
        }
    }
}

@Composable
private fun FinishEffect(vm: GameViewModel, chapter: ChapterId, finished: Boolean, mistakes: Int, delayMs: Long = 650) {
    LaunchedEffect(finished) {
        if (finished) {
            delay(delayMs)
            vm.completeChapter(chapter, GameRules.starsForMistakes(mistakes))
        }
    }
}

@Composable
private fun FindMissingGame(vm: GameViewModel, chapter: ChapterId) {
    val t = vm.text
    val game = vm.gameText.find
    var mistakes by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    FinishEffect(vm, chapter, finished, mistakes)
    Text(game.instruction, fontSize = scaledSp(16f), color = Cream, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
    Spacer(Modifier.height(14.dp))
    game.options.forEachIndexed { index, option ->
        KingdomButton(
            text = option,
            onClick = {
                if (index == game.correctIndex) {
                    vm.playCorrect()
                    finished = true
                } else {
                    vm.playWrong()
                    mistakes++
                    vm.showMessage(t.notQuite)
                }
            },
            color = if (index == game.correctIndex && finished) Mint else PanelLight,
        )
        Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(6.dp))
    HintButton(t.hint, game.hint, hint) { hint = !hint; vm.playTap() }
}

@Composable
private fun InstructionOrderGame(vm: GameViewModel, chapter: ChapterId) {
    val t = vm.text
    val game = vm.gameText.order
    val order = remember { mutableStateListOf(1, 0, 2) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var mistakes by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    FinishEffect(vm, chapter, finished, mistakes)
    Text(game.instruction, fontSize = scaledSp(15f), color = Cream, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
    Spacer(Modifier.height(14.dp))
    order.forEachIndexed { position, stepIndex ->
        val active = selected == position
        PanelCard(
            Modifier
                .fillMaxWidth()
                .clickable {
                    if (finished) return@clickable
                    vm.playTap()
                    val first = selected
                    if (first == null) {
                        selected = position
                    } else if (first == position) {
                        selected = null
                    } else {
                        val temp = order[first]
                        order[first] = order[position]
                        order[position] = temp
                        selected = null
                        if (order == listOf(0, 1, 2)) {
                            finished = true
                        } else {
                            mistakes++
                        }
                    }
                }
                .alpha(if (active) 1f else 0.92f),
            containerColor = if (active) Indigo else Panel,
        ) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${position + 1}", fontSize = scaledSp(18f), fontWeight = FontWeight.Bold, color = Gold)
                Spacer(Modifier.width(14.dp))
                Text(game.steps[stepIndex], fontSize = scaledSp(15f), color = Cream, modifier = Modifier.weight(1f))
                Text("↕", fontSize = scaledSp(18f), color = Cyan)
            }
        }
        Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(6.dp))
    HintButton(t.hint, game.hint, hint) { hint = !hint; vm.playTap() }
}

@Composable
private fun MemoryDeskGame(vm: GameViewModel, chapter: ChapterId) {
    val t = vm.text
    val game = vm.gameText.memory
    var deskSize by remember { mutableIntStateOf(2) }
    val tasks = remember { mutableStateListOf<String>() }
    var powered by remember { mutableStateOf(true) }
    var cleared by remember { mutableStateOf(false) }
    var mistakes by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    FinishEffect(vm, chapter, finished, mistakes)
    Text(game.instruction, fontSize = scaledSp(15f), color = Cream, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
    Spacer(Modifier.height(12.dp))
    PanelCard(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (powered) game.powerOn else game.powerOff, fontSize = scaledSp(13f), color = if (powered) Mint else Coral)
            Spacer(Modifier.height(10.dp))
            for (row in 0 until 2) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (column in 0 until deskSize / 2) {
                        val slot = row * (deskSize / 2) + column
                        Box(
                            Modifier
                                .size(74.dp)
                                .alpha(if (slot < tasks.size) 1f else 0.35f),
                            contentAlignment = Alignment.Center,
                        ) {
                            PanelCard(Modifier.size(74.dp), containerColor = if (slot < tasks.size) Gold else PanelLight) {
                                Box(Modifier.size(74.dp), contentAlignment = Alignment.Center) {
                                    Text(if (slot < tasks.size) "★" else "·", fontSize = scaledSp(24f), color = Ink)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            Text("${game.placed}: ${tasks.size} / $deskSize", fontSize = scaledSp(13f), color = Muted)
        }
    }
    Spacer(Modifier.height(12.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ChoiceGameButton(game.smallDesk, deskSize == 2) { deskSize = 2 }
        ChoiceGameButton(game.largeDesk, deskSize == 4) { deskSize = 4 }
    }
    Spacer(Modifier.height(10.dp))
    KingdomButton(game.addTask, {
        vm.playTap()
        when {
            !powered -> vm.showMessage(game.powerOn)
            tasks.size >= deskSize -> {
                mistakes++
                vm.showMessage(game.full)
                hint = true
            }
            else -> tasks.add("task")
        }
    }, color = Cyan, contentColor = Ink)
    Spacer(Modifier.height(10.dp))
    KingdomButton(if (powered) game.powerOff else game.powerOn, {
        vm.playTap()
        if (powered) {
            if (tasks.size < 3) {
                vm.showMessage(game.instruction)
            } else {
                powered = false
                tasks.clear()
                cleared = true
                finished = true
            }
        } else {
            powered = true
            cleared = false
        }
    }, color = Gold, contentColor = Ink)
    if (cleared) {
        Spacer(Modifier.height(8.dp))
        PanelCard(Modifier.fillMaxWidth()) {
            Text(game.cleared, modifier = Modifier.padding(14.dp), fontSize = scaledSp(13f), color = Cream)
        }
    }
    Spacer(Modifier.height(10.dp))
    HintButton(t.hint, game.hint, hint) { hint = !hint; vm.playTap() }
}

@Composable
private fun StorageSortGame(vm: GameViewModel, chapter: ChapterId) {
    val t = vm.text
    val game = vm.gameText.sort
    val remaining = remember { mutableStateListOf<SortItem>().apply { addAll(game.items) } }
    var selected by remember { mutableStateOf<SortItem?>(null) }
    var mistakes by remember { mutableIntStateOf(0) }
    var hint by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    FinishEffect(vm, chapter, finished, mistakes)
    Text(game.instruction, fontSize = scaledSp(15f), color = Cream, modifier = Modifier.padding(horizontal = 16.dp), textAlign = TextAlign.Center)
    Spacer(Modifier.height(12.dp))
    Text("${t.score}: ${game.items.size - remaining.size} / ${game.items.size}", fontSize = scaledSp(13f), color = Muted, modifier = Modifier.padding(horizontal = 16.dp))
    Spacer(Modifier.height(10.dp))
    remaining.forEach { item ->
        val active = selected == item
        PanelCard(
            Modifier
                .fillMaxWidth()
                .clickable { selected = if (active) null else item; vm.playTap() },
            containerColor = if (active) Indigo else Panel,
        ) {
            Text(item.label, modifier = Modifier.padding(16.dp), fontSize = scaledSp(15f), color = Cream)
        }
        Spacer(Modifier.height(8.dp))
    }
    if (remaining.isEmpty()) {
        Text(t.complete, fontSize = scaledSp(17f), fontWeight = FontWeight.Bold, color = Mint)
        Spacer(Modifier.height(10.dp))
    }
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        KingdomButton(game.ramBin, {
            placeItem(selected, true, remaining) { correct ->
                if (correct) vm.playCorrect() else { vm.playWrong(); mistakes++; vm.showMessage(t.notQuite) }
                selected = null
                if (remaining.isEmpty()) finished = true
            }
        }, Modifier.weight(1f), color = Cyan, contentColor = Ink)
        KingdomButton(game.storageBin, {
            placeItem(selected, false, remaining) { correct ->
                if (correct) vm.playCorrect() else { vm.playWrong(); mistakes++; vm.showMessage(t.notQuite) }
                selected = null
                if (remaining.isEmpty()) finished = true
            }
        }, Modifier.weight(1f), color = Mint, contentColor = Ink)
    }
    Spacer(Modifier.height(10.dp))
    HintButton(t.hint, game.hint, hint) { hint = !hint; vm.playTap() }
}

private fun placeItem(
    selected: SortItem?,
    toRam: Boolean,
    remaining: MutableList<SortItem>,
    onResult: (Boolean) -> Unit,
) {
    if (selected == null) return
    val correct = selected.inRam == toRam
    if (correct) remaining.remove(selected)
    onResult(correct)
}

@Composable
private fun RowScope.ChoiceGameButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp).weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Indigo else PanelLight,
            contentColor = if (selected) Cream else Muted,
        ),
    ) {
        Text(label, fontSize = scaledSp(13f), fontWeight = FontWeight.Bold)
    }
}
