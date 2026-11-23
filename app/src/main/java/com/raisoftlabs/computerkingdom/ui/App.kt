package com.raisoftlabs.computerkingdom.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.raisoftlabs.computerkingdom.game.GameViewModel
import com.raisoftlabs.computerkingdom.game.Screen
import kotlinx.coroutines.delay

@Composable
fun ComputerKingdomApp(vm: GameViewModel) {
    val text = vm.text
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            vm.checkPlayTime()
        }
    }
    CompositionLocalProvider(LocalTextScale provides vm.settings.textScale) {
        KingdomBackdrop {
            Box(Modifier.fillMaxSize()) {
                when (val screen = vm.screen) {
                    Screen.Splash -> SplashScreen(text.appTitle, text.tagline) { vm.navigate(Screen.Menu) }
                    Screen.Menu -> MainMenuScreen(vm)
                    Screen.Map -> WorldMapScreen(vm)
                    Screen.Explore -> ExploreScreen(vm)
                    Screen.Collection -> CollectionScreen(vm)
                    Screen.ParentGate -> ParentGateScreen(vm)
                    Screen.Parent -> ParentDashboardScreen(vm)
                    Screen.Settings -> SettingsScreen(vm)
                    is Screen.Story -> DialogueScreen(vm, screen)
                    is Screen.Lesson -> LessonScreen(vm, screen.chapter)
                    is Screen.Game -> MiniGameScreen(vm, screen)
                    is Screen.Reward -> RewardScreen(vm, screen)
                }
                vm.toast?.let { message -> ToastMessage(message) { vm.clearMessage() } }
                if (vm.showBreak) BreakDialog(vm)
            }
        }
    }
}
