package diego.com.pedidos.adapter.out.persistence

import diego.com.pedidos.application.port.out.PedidoRepositoryPort
import diego.com.pedidos.domain.EstadoPedido
import diego.com.pedidos.domain.Pedido
import org.springframework.stereotype.Repository

@Repository
class PedidoJpaAdapter(private val repository: SpringDataPedidoRepository) : PedidoRepositoryPort {
    override fun tienePedidoPendiente(cliente: String) =
        repository.existsByClienteAndEstado(cliente, EstadoPedido.PENDIENTE)

    override fun guardar(pedido: Pedido) = repository.save(pedido)

    override fun buscarPorCodigo(codigoPedido: String) =
        repository.findAllByCodigoPedidoOrderByIdAsc(codigoPedido)
}
