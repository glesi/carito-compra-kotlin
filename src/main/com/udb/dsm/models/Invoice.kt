package main.com.udb.dsm.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Invoice(
    val id: String = generateInvoiceId(),
    val date: LocalDateTime = LocalDateTime.now(),
    val items: List<InvoiceItem>,
    val subtotal: Double,
    val taxRate: Double = 0.13, // 13% de IVA
    val tax: Double = subtotal * taxRate,
    val total: Double = subtotal + tax
) {
    fun print() {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        val lineSeparator = "=".repeat(60)

        println(lineSeparator)
        println("                     FACTURA")
        println(lineSeparator)
        println("No. Factura: $id")
        println("Fecha: ${date.format(formatter)}")
        println(lineSeparator)
        println("%-4s %-25s %-8s %-8s %-10s".format("Cant", "Producto", "Precio", "Total", ""))
        println(lineSeparator)

        items.forEach { item ->
            println("%-4d %-25s $%-7.2f $%-8.2f".format(
                item.quantity,
                item.product.name.take(25),
                item.product.price,
                item.total
            ))
        }

        println(lineSeparator)
        println("%-41s $%-8.2f".format("Subtotal:", subtotal))
        println("%-41s $%-8.2f".format("IVA (${(taxRate * 100).toInt()}%):", tax))
        println("%-41s $%-8.2f".format("TOTAL:", total))
        println(lineSeparator)
        println("              ¡Gracias por su compra!")
        println(lineSeparator)
    }

    companion object {
        private var lastInvoiceNumber = 0

        fun generateInvoiceId(): String {
            lastInvoiceNumber++
            return "INV-${String.format("%05d", lastInvoiceNumber)}"
        }
    }
}

data class InvoiceItem(
    val product: Product,
    val quantity: Int,
    val price: Double = product.price,
    val total: Double = price * quantity
)