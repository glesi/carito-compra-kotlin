package main.com.udb.dsm.services

import main.com.udb.dsm.models.Product
import main.com.udb.dsm.models.Invoice
import main.com.udb.dsm.models.InvoiceItem

/// Esta clase es el corazon y logica del carrito, aca se pueden ir agregando todas las compras

class ShoppingCartService(private val productService: ProductService) {
    private val cart = mutableListOf<Pair<Product, Int>>()

    fun addItem(product: Product, quantity: Int = 1): Boolean {

        if (!productService.isProductAvailable(product.id, quantity)) {
            println("No hay suficiente stock disponible. Stock actual: ${product.stock}")
            return false
        }

        val existingItem = cart.find { it.first.id == product.id }
        if (existingItem != null) {

            val index = cart.indexOf(existingItem)
            cart[index] = Pair(product, existingItem.second + quantity)
        } else {

            cart.add(Pair(product, quantity))
        }
        println("${quantity} ${product.name}(s) añadido(s) al carrito.")
        return true
    }

    fun removeItem(product: Product, quantity: Int = 1) {
        val existingItem = cart.find { it.first.id == product.id }
        if (existingItem != null) {
            val index = cart.indexOf(existingItem)
            val newQuantity = existingItem.second - quantity
            if (newQuantity > 0) {

                cart[index] = Pair(product, newQuantity)
                println("Cantidad de ${product.name} reducida en ${quantity}. Cantidad actual: ${newQuantity}")
            } else {

                cart.removeAt(index)
                println("${product.name} eliminado del carrito.")
            }
        } else {
            println("${product.name} no está en el carrito.")
        }
    }

    fun getCartItems(): List<Pair<Product, Int>> {
        return cart.toList()
    }

    fun calculateSubtotal(): Double {
        return cart.sumOf { it.first.price * it.second }
    }

    fun calculateTax(subtotal: Double, taxRate: Double = 0.13): Double {
        return subtotal * taxRate
    }

    fun calculateTotal(subtotal: Double, tax: Double): Double {
        return subtotal + tax
    }

    fun generateInvoice(): Invoice {
        val subtotal = calculateSubtotal()
        val taxRate = 0.13
        val tax = calculateTax(subtotal, taxRate)

        val invoiceItems = cart.map { (product, quantity) ->
            InvoiceItem(product, quantity)
        }

        return Invoice(
            items = invoiceItems,
            subtotal = subtotal,
            taxRate = taxRate,
            tax = tax,
            total = calculateTotal(subtotal, tax)
        )
    }

    fun checkout(): Invoice? {
        if (cart.isEmpty()) {
            println("El carrito está vacío. No se puede generar factura.")
            return null
        }


        cart.forEach { (product, quantity) ->
            productService.updateProductStock(product.id, quantity)
        }

        // Generar factura
        val invoice = generateInvoice()


        clearCart()

        return invoice
    }

    fun clearCart() {
        cart.clear()
        println("Carrito vaciado.")
    }

    fun printCart() {
        if (cart.isEmpty()) {
            println("El carrito está vacío.")
        } else {
            val subtotal = calculateSubtotal()
            val tax = calculateTax(subtotal)
            val total = calculateTotal(subtotal, tax)

            println("--- Carrito de Compras ---")
            println("%-4s %-25s %-8s %-8s %-10s".format("Cant", "Producto", "Precio", "Total", ""))
            println("-".repeat(60))

            cart.forEach { (product, quantity) ->
                println("%-4d %-25s $%-7.2f $%-8.2f".format(
                    quantity,
                    product.name.take(25),
                    product.price,
                    product.price * quantity
                ))
            }

            println("-".repeat(60))
            println("%-41s $%-8.2f".format("Subtotal:", subtotal))
            println("%-41s $%-8.2f".format("IVA (13%):", tax))
            println("%-41s $%-8.2f".format("Total:", total))
        }
    }

    fun isEmpty(): Boolean {
        return cart.isEmpty()
    }
}