package main.com.udb.dsm.util

import java.io.File

object FileUtils {
    fun readProductsFromFile(filePath: String): List<String> {
        val file = File(filePath)
        return if (file.exists()) {
            file.readLines()
        } else {
            println("¡Advertencia! El archivo no existe: $filePath")
            emptyList()
        }
    }

    fun writeProductsToFile(filePath: String, lines: List<String>) {
        val file = File(filePath)
        file.writeText(lines.joinToString("\n"))
    }
}