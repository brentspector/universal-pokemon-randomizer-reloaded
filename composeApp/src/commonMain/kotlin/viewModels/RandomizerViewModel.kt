package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import logicModules.Randomizer
import romHandlers.Gen1RomHandler

object RandomizerViewModel {
    var randomizer by mutableStateOf(Randomizer(Gen1RomHandler()))
}