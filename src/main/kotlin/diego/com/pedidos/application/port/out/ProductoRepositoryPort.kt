package diego.com.pedidos.application.port.out

import diego.com.pedidos.domain.Producto

interface ProductoRepositoryPort {
    fun buscarPorNombre(nombre: String): Producto?
    fun buscarPorNombreParaActualizar(nombre: String): Producto?
    fun guardar(producto: Producto): Producto
}
