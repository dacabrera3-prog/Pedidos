package diego.com.pedidos.application.dto

import diego.com.pedidos.domain.EstadoPedido

data class CrearProductoRequest(
    val nombre: String,
    val descripcion: String,
    val stock: Int
)

data class ProductoResponse(
    val nombre: String,
    val descripcion: String,
    val stock: Int
)

data class ItemPedidoRequest(
    val producto: String,
    val cantidad: Int
)

data class CrearPedidoRequest(
    val cliente: String,
    val items: List<ItemPedidoRequest>
)

data class RecibirPedidoRequest(
    val codigoVerificacion: String
)

data class ItemPedidoResponse(
    val producto: String,
    val cantidad: Int
)

data class PedidoCreadoResponse(
    val codigoPedido: String,
    val codigoVerificacion: String,
    val cliente: String,
    val items: List<ItemPedidoResponse>,
    val estado: EstadoPedido
)

data class PedidoResponse(
    val codigoPedido: String,
    val cliente: String,
    val items: List<ItemPedidoResponse>,
    val estado: EstadoPedido
)
