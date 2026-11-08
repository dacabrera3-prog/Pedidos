package diego.com.pedidos.adapter.out.persistence

import diego.com.pedidos.application.port.out.ProductoRepositoryPort
import diego.com.pedidos.domain.Producto
import org.springframework.stereotype.Repository

@Repository
class ProductoJpaAdapter(private val repository: SpringDataProductoRepository) : ProductoRepositoryPort {
    override fun buscarPorNombre(nombre: String) = repository.findById(nombre).orElse(null)

    override fun buscarPorNombreParaActualizar(nombre: String) = repository.findByNombreForUpdate(nombre)

    override fun guardar(producto: Producto) = repository.save(producto)
}
