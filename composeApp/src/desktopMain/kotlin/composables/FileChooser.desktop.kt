package composables

import java.awt.FileDialog
import java.io.File
import java.io.FileOutputStream
import java.io.FilenameFilter
import javax.swing.JFrame

actual fun getFile(byteArray: ByteArray) {
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
    fileDialog.filenameFilter = ROMFilter()
    fileDialog.file = "mything.txt"
    fileDialog.isVisible = true
    try {
        val fh = File(fileDialog.directory + fileDialog.file)
        fh.createNewFile()
        val fos = FileOutputStream(fh)
        fos.write("Overwritten at ${System.currentTimeMillis()}\n"
            .toByteArray())
        fos.close()
    } catch (t : Throwable) {
        println(t)
        println("No File")
    }
}

class ROMFilter : FilenameFilter {
    override fun accept(p0: File?, p1: String?): Boolean {
        if (p0 != null) {
            return true // needed to allow directory navigation
        }
        if (p1 != null) {
            if (!p1.contains(".")) {
                return false
            }
            val extension: String =
                p1.substring(p1.lastIndexOf('.') + 1).lowercase()
            return extension == "gb" || extension == "sgb" || extension == "gbc" || extension == "gba" || extension == "nds"
        }
        return false
    }

}