package main.com.udb.dsm

import main.com.udb.dsm.models.Product
import main.com.udb.dsm.services.ProductService
import main.com.udb.dsm.services.ShoppingCartService
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    val productService = ProductService("src/resources/products.txt")
    val shoppingCartService = ShoppingCartService(productService)

    val products = productService.getAllProducts()

    if (products.isEmpty()) {
        println("No se encontraron productos. Asegúrate de que el archivo de productos exista y tenga el formato correcto.")
        return
    }

    var continueShop = true
    while (continueShop) {
        displayMenu()
        print("Elige una opción: ")

        when (readlnOrNull()?.toIntOrNull()) {
            1 -> listProducts(products)
            2 -> addToCart(productService, shoppingCartService, scanner)
            3 -> shoppingCartService.printCart()
            4 -> removeFromCart(productService, shoppingCartService, scanner)
            5 -> shoppingCartService.clearCart()
            6 -> {
                continueShop = false
                println("¡Gracias por visitarnos!")
            }
            7 -> {
                val invoice = checkout(shoppingCartService)
                if (invoice != null) {

                    print("¿Desea realizar otra compra? (s/n): ")
                    continueShop = readlnOrNull()?.lowercase() == "s"
                    if (!continueShop) {
                        println("¡Gracias por su compra!")
                    }
                }
            }
            else -> println("Opción inválida.")
        }
    }
}

fun displayMenu() {
    println("\n--- Menú del Sistema de Compras ---")
    println("1. Listar Productos Disponibles")
    println("2. Añadir Producto al Carrito")
    println("3. Ver Carrito")
    println("4. Eliminar Producto del Carrito")
    println("5. Vaciar Carrito")
    println("6. Salir")
    println("7. Realizar Compra y Generar Factura")
}

fun listProducts(products: List<Product>) {
    println("\n--- Productos Disponibles ---")
    if (products.isEmpty()) {
        println("No hay productos disponibles.")
    } else {
        println("%-4s %-30s %-40s %-8s %-6s".format("ID", "Nombre", "Descripción", "Precio", "Stock"))
        println("-".repeat(95))
        products.forEach { product ->
            println("%-4d %-30s %-40s $%-7.2f %-6d".format(
                product.id,
                product.name.take(30),
                product.description.take(40),
                product.price,
                product.stock
            ))
        }
    }
}

fun addToCart(productService: ProductService, shoppingCartService: ShoppingCartService, scanner: Scanner) {
    print("Introduce el ID del producto que quieres añadir al carrito: ")
    val productId = readLine()?.toIntOrNull()
    if (productId == null) {
        println("ID de producto inválido.")
        return
    }

    val product = productService.getProductById(productId)
    if (product == null) {
        println("Producto no encontrado.")
        return
    }

    if (product.stock <= 0) {
        println("Lo sentimos, el producto ${product.name} está agotado.")
        return
    }

    print("Introduce la cantidad (disponibles: ${product.stock}): ")
    val quantityStr = readLine()
    val quantity = if (quantityStr.isNullOrEmpty()) 1 else quantityStr.toIntOrNull() ?: 1

    if (quantity <= 0) {
        println("La cantidad debe ser mayor que 0.")
        return
    }

    if (quantity > product.stock) {
        println("No hay suficiente stock disponible. Stock actual: ${product.stock}")
        return
    }

    shoppingCartService.addItem(product, quantity)
}

fun removeFromCart(productService: ProductService, shoppingCartService: ShoppingCartService, scanner: Scanner) {
    if (shoppingCartService.isEmpty()) {
        println("El carrito está vacío.")
        return
    }

    println("\n--- Productos en el Carrito ---")
    shoppingCartService.printCart()

    print("Introduce el ID del producto que quieres eliminar del carrito: ")
    val productId = readLine()?.toIntOrNull()
    if (productId == null) {
        println("ID de producto inválido.")
        return
    }

    val product = productService.getProductById(productId)
    if (product == null) {
        println("Producto no encontrado.")
        return
    }

    print("Introduce la cantidad a eliminar: ")
    val quantityStr = readLine()
    val quantity = if (quantityStr.isNullOrEmpty()) 1 else quantityStr.toIntOrNull() ?: 1

    if (quantity <= 0) {
        println("La cantidad debe ser mayor que 0.")
        return
    }

    shoppingCartService.removeItem(product, quantity)
}

fun checkout(shoppingCartService: ShoppingCartService): main.com.udb.dsm.models.Invoice? {
    if (shoppingCartService.isEmpty()) {
        println("El carrito está vacío. No hay nada que comprar.")
        return null
    }

    println("\n--- Resumen de la Compra ---")
    shoppingCartService.printCart()

    print("¿Confirmar la compra? (s/n): ")
    val confirmation = readlnOrNull()?.lowercase()

    if (confirmation == "s") {
        val invoice = shoppingCartService.checkout()
        if (invoice != null) {
            println("\n¡Compra realizada con éxito!")
            invoice.print()
            return invoice
        } else {
            println("Error al generar la factura.")
            return null
        }
    } else {
        println("Compra cancelada.")
        return null
    }
}