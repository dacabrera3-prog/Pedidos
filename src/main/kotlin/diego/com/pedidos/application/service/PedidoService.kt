package diego.com.pedidos.application.service

import diego.com.pedidos.application.dto.CrearPedidoRequest
import diego.com.pedidos.application.dto.ItemPedidoResponse
import diego.com.pedidos.application.dto.PedidoCreadoResponse
import diego.com.pedidos.application.dto.PedidoResponse
import diego.com.pedidos.application.exception.ClienteConPedidoPendienteException
import diego.com.pedidos.application.exception.CodigoVerificacionIncorrectoException
import diego.com.pedidos.application.exception.EstadoInvalidoException
import diego.com.pedidos.application.exception.PedidoNoExisteException
import diego.com.pedidos.application.exception.ProductoNoExisteException
import diego.com.pedidos.application.exception.SolicitudInvalidaException
import diego.com.pedidos.application.exception.StockInsuficienteException
import diego.com.pedidos.application.port.out.PedidoRepositoryPort
import diego.com.pedidos.application.port.out.ProductoRepositoryPort
import diego.com.pedidos.domain.EstadoPedido
import diego.com.pedidos.domain.Pedido
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.util.UUID

@Service
class PedidoService(
    private val pedidos: PedidoRepositoryPort,
    private val productos: ProductoRepositoryPort
) {
    private val random = SecureRandom()

    @Transactional
    fun crear(request: CrearPedidoRequest): PedidoCreadoResponse {
        if (request.cliente.isBlank()) throw SolicitudInvalidaException("El cliente es obligatorio")
        if (request.items.isEmpty()) throw SolicitudInvalidaException("El pedido debe incluir productos")
        if (request.items.any { it.producto.isBlank() || it.cantidad <= 0 }) {
            throw SolicitudInvalidaException("Cada producto debe tener nombre y cantidad positiva")
        }
        if (pedidos.tienePedidoPendiente(request.cliente)) {
            throw ClienteConPedidoPendienteException(request.cliente)
        }

        val cantidades = request.items.groupingBy { it.producto }.fold(0) { total, item -> total + item.cantidad }
        val productosBloqueados = cantidades.keys.sorted().associateWith { nombre ->
            productos.buscarPorNombreParaActualizar(nombre) ?: throw ProductoNoExisteException(nombre)
        }
        cantidades.forEach { (nombre, cantidad) ->
            val producto = productosBloqueados.getValue(nombre)
            if (producto.stock < cantidad) {
                throw StockInsuficienteException(nombre, producto.stock, cantidad)
            }
        }

        val codigoPedido = "PED-${UUID.randomUUID().toString().take(8).uppercase()}"
        val codigoVerificacion = "%06d".format(random.nextInt(1_000_000))
        cantidades.forEach { (nombre, cantidad) ->
            val producto = productosBloqueados.getValue(nombre)
            producto.stock -= cantidad
            productos.guardar(producto)
            pedidos.guardar(
                Pedido(
                    codigoPedido = codigoPedido,
                    cliente = request.cliente,
                    nombreProducto = nombre,
                    cantidad = cantidad,
                    estado = EstadoPedido.PENDIENTE,
                    codigoVerificacion = codigoVerificacion
                )
            )
        }
        return PedidoCreadoResponse(
            codigoPedido = codigoPedido,
            codigoVerificacion = codigoVerificacion,
            cliente = request.cliente,
            items = cantidades.map { (nombre, cantidad) -> ItemPedidoResponse(nombre, cantidad) },
            estado = EstadoPedido.PENDIENTE
        )
    }

    @Transactional
    fun enviar(codigoPedido: String): PedidoResponse {
        val filas = buscar(codigoPedido)
        if (filas.any { it.estado != EstadoPedido.PENDIENTE }) {
            throw EstadoInvalidoException("Solo un pedido PENDIENTE se puede enviar")
        }
        filas.forEach { it.estado = EstadoPedido.ENVIADO }
        filas.forEach { pedidos.guardar(it) }
        return respuesta(filas)
    }

    @Transactional
    fun recibir(codigoPedido: String, codigoVerificacion: String): PedidoResponse {
        val filas = buscar(codigoPedido)
        if (filas.any { it.estado != EstadoPedido.ENVIADO }) {
            throw EstadoInvalidoException("Solo un pedido ENVIADO se puede recibir")
        }
        if (filas.any { it.codigoVerificacion != codigoVerificacion }) {
            throw CodigoVerificacionIncorrectoException()
        }
        filas.forEach { it.estado = EstadoPedido.RECIBIDO }
        filas.forEach { pedidos.guardar(it) }
        return respuesta(filas)
    }

    private fun buscar(codigoPedido: String): List<Pedido> =
        pedidos.buscarPorCodigo(codigoPedido).takeIf { it.isNotEmpty() }
            ?: throw PedidoNoExisteException(codigoPedido)

    private fun respuesta(filas: List<Pedido>) = PedidoResponse(
        codigoPedido = filas.first().codigoPedido,
        cliente = filas.first().cliente,
        items = filas.map { ItemPedidoResponse(it.nombreProducto, it.cantidad) },
        estado = filas.first().estado
    )
}
