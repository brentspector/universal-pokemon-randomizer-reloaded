package viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import logicModules.Randomizer
import romHandlers.Gen1RomHandler
import romHandlers.Gen2RomHandler
import romHandlers.RomHandler
import kotlin.reflect.KFunction1


object RandomizerViewModel {
    var randomizer by mutableStateOf(Randomizer(getRandomizer("Gen1")))
}

private val romHandlers: Map<String, Pair<() -> RomHandler, KFunction1<String, Boolean>>> = mapOf(
    "Gen1RomHandler" to Pair({ Gen1RomHandler() }, Gen1RomHandler::isLoadable),
    "Gen2RomHandler" to Pair({ Gen2RomHandler() }, Gen2RomHandler::isLoadable)
)

private fun getRandomizer(romHandler: String): RomHandler {
    val (handlerFactory, loadableCheck) = romHandlers[romHandler]
        ?: throw Exception("No ROMHandler Found")

    if (loadableCheck(romHandler)) {
        return handlerFactory()
    } else {
        throw Exception("ROMHandler $romHandler is not loadable.")
    }
}