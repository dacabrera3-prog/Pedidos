package diego.com.pedidos.application.port.out

import diego.com.pedidos.domain.Pedido

interface PedidoRepositoryPort {
    fun tienePedidoPendiente(cliente: String): Boolean
    fun guardar(pedido: Pedido): Pedido
    fun buscarPorCodigo(codigoPedido: String): List<Pedido>
}
