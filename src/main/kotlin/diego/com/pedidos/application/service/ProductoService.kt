package diego.com.pedidos.application.service

import diego.com.pedidos.application.dto.CrearProductoRequest
import diego.com.pedidos.application.dto.ProductoResponse
import diego.com.pedidos.application.exception.ProductoDuplicadoException
import diego.com.pedidos.application.exception.SolicitudInvalidaException
import diego.com.pedidos.application.port.out.ProductoRepositoryPort
import diego.com.pedidos.domain.Producto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductoService(private val productos: ProductoRepositoryPort) {

    @Transactional
    fun crear(request: CrearProductoRequest): ProductoResponse {
        if (request.nombre.isBlank()) throw SolicitudInvalidaException("El nombre es obligatorio")
        if (request.stock < 0) throw SolicitudInvalidaException("El stock no puede ser negativo")
        if (productos.buscarPorNombre(request.nombre) != null) {
            throw ProductoDuplicadoException(request.nombre)
        }
        val producto = productos.guardar(
            Producto(nombre = request.nombre, descripcion = request.descripcion, stock = request.stock)
        )
        return ProductoResponse(producto.nombre, producto.descripcion, producto.stock)
    }
}
