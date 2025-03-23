package main.com.udb.dsm.services

import main.com.udb.dsm.models.Product
import main.com.udb.dsm.util.FileUtils

class ProductService(private val filePath: String) {
    private var products: MutableList<Product> = mutableListOf()

    init {
        loadProducts()
    }

    fun loadProducts() {
        val lines = FileUtils.readProductsFromFile(filePath)
        products = lines.mapNotNull { line ->
            try {
                val parts = line.split(",")
                if (parts.size == 5) {
                    val id = parts[0].trim().toInt()
                    val name = parts[1].trim()
                    val description = parts[2].trim()
                    val price = parts[3].trim().toDouble()
                    val stock = parts[4].trim().toInt()
                    Product(id, name, description, price, stock)
                } else {
                    println("Línea de producto inválida: $line")
                    null
                }
            } catch (e: NumberFormatException) {
                println("Error al parsear la línea: $line - ${e.message}")
                null
            }
        }.toMutableList()
    }

    fun getAllProducts(): List<Product> {
        return products.toList()
    }

    fun getProductById(id: Int): Product? {
        return products.find { it.id == id }
    }

    fun updateProductStock(id: Int, quantity: Int): Boolean {
        val product = getProductById(id)
        if (product != null && product.stock >= quantity) {
            product.stock -= quantity
            saveProducts()
            return true
        }
        return false
    }

    fun increaseProductStock(id: Int, quantity: Int): Boolean {
        val product = getProductById(id)
        if (product != null) {
            product.stock += quantity
            saveProducts()
            return true
        }
        return false
    }

    fun isProductAvailable(id: Int, quantity: Int): Boolean {
        val product = getProductById(id)
        return product != null && product.stock >= quantity
    }

    private fun saveProducts() {
        val lines = products.map { "${it.id},${it.name},${it.description},${it.price},${it.stock}" }
        FileUtils.writeProductsToFile(filePath, lines)
    }
}