package main.com.udb.dsm.models

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    var stock: Int
) {
    //Campos minimos que pedia el ejercicio.
    override fun toString(): String {
        return "ID: $id, Nombre: $name, Descripción: $description, Precio: $${price}, Disponibles: $stock"
    }
}