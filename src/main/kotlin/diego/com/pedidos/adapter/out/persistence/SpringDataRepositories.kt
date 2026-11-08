package diego.com.pedidos.adapter.out.persistence

import diego.com.pedidos.domain.EstadoPedido
import diego.com.pedidos.domain.Pedido
import diego.com.pedidos.domain.Producto
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SpringDataPedidoRepository : JpaRepository<Pedido, Long> {
    fun existsByClienteAndEstado(cliente: String, estado: EstadoPedido): Boolean
    fun findAllByCodigoPedidoOrderByIdAsc(codigoPedido: String): List<Pedido>
}

interface SpringDataProductoRepository : JpaRepository<Producto, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.nombre = :nombre")
    fun findByNombreForUpdate(@Param("nombre") nombre: String): Producto?
}
