package diego.com.pedidos.adapter.out.memory

import diego.com.pedidos.application.port.out.PedidoRepositoryPort
import diego.com.pedidos.domain.EstadoPedido
import diego.com.pedidos.domain.Pedido

class PedidosEnMemoria : PedidoRepositoryPort {
    private val filas = mutableListOf<Pedido>()

    override fun tienePedidoPendiente(cliente: String): Boolean =
        filas.any { it.cliente == cliente && it.estado == EstadoPedido.PENDIENTE }

    override fun guardar(pedido: Pedido): Pedido {
        val indice = pedido.id?.let { id -> filas.indexOfFirst { it.id == id } } ?: -1
        if (indice >= 0) {
            filas[indice] = pedido
        } else {
            val nuevoId = (filas.mapNotNull { it.id }.maxOrNull() ?: 0L) + 1L
            pedido.id = nuevoId
            filas.add(pedido)
        }
        return pedido
    }

    override fun buscarPorCodigo(codigoPedido: String): List<Pedido> =
        filas.filter { it.codigoPedido == codigoPedido }
}
