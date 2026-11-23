package com.raisoftlabs.computerkingdom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.raisoftlabs.computerkingdom.game.GameViewModel
import com.raisoftlabs.computerkingdom.game.Narrator
import com.raisoftlabs.computerkingdom.ui.ComputerKingdomApp

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        Narrator.init(this, viewModel.settings.language)
        setContent {
            ComputerKingdomApp(viewModel)
        }
    }

    override fun onDestroy() {
        Narrator.stop()
        super.onDestroy()
    }
}
