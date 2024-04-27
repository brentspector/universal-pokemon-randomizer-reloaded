package composables

import java.awt.FileDialog
import javax.swing.JFileChooser
import javax.swing.JFrame

actual fun getFile() {
    awtFileDialogImpl()
    jFilChooserImpl()
}

fun jFilChooserImpl() {
    val fileChooser = JFileChooser()
    if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        println(fileChooser.selectedFile.absolutePath)
    }
}

fun awtFileDialogImpl() {
    val fileDialog = FileDialog(JFrame(), "Select ROM to load")
    fileDialog.isVisible = true
    try {
        println(fileDialog.file)
    } catch (t : Throwable) {
        println("No File")
    }
}