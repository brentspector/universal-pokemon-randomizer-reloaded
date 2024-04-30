package composables

import java.awt.FileDialog
import javax.swing.JFrame

actual fun getFile() {
    val fileDialog = FileDialog(JFrame(), "Select ROM to load", FileDialog.LOAD)
    fileDialog.isVisible = true
    try {
        println(fileDialog.file)
    } catch (t : Throwable) {
        println("No File")
    }
}

actual fun saveFile() {
    val fileDialog = FileDialog(JFrame(), "Save ROM", FileDialog.SAVE)
    fileDialog.isVisible = true
    try {
        println(fileDialog.file)
    } catch (t : Throwable) {
        println("No File")
    }
}