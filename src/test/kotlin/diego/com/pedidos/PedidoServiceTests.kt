package diego.com.pedidos

import diego.com.pedidos.adapter.out.memory.PedidosEnMemoria
import diego.com.pedidos.application.dto.CrearPedidoRequest
import diego.com.pedidos.application.dto.ItemPedidoRequest
import diego.com.pedidos.application.exception.ClienteConPedidoPendienteException
import diego.com.pedidos.application.exception.CodigoVerificacionIncorrectoException
import diego.com.pedidos.application.exception.EstadoInvalidoException
import diego.com.pedidos.application.exception.ProductoNoExisteException
import diego.com.pedidos.application.exception.StockInsuficienteException
import diego.com.pedidos.application.port.out.ProductoRepositoryPort
import diego.com.pedidos.application.service.PedidoService
import diego.com.pedidos.domain.EstadoPedido
import diego.com.pedidos.domain.Producto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PedidoServiceTests {
    private val pedidos = PedidosEnMemoria()
    private val productos = ProductosEnMemoria(
        listOf(
            Producto("Cuaderno", "Cuaderno universitario", 10),
            Producto("Lapiz", "Lapiz HB", 5)
        )
    )
    private val servicio = PedidoService(pedidos, productos)

    @Test
    fun `crear descuenta stock y genera codigo de seis digitos`() {
        val creado = servicio.crear(
            CrearPedidoRequest("Ana", listOf(ItemPedidoRequest("Cuaderno", 2), ItemPedidoRequest("Lapiz", 3)))
        )

        assertEquals(EstadoPedido.PENDIENTE, creado.estado)
        assertTrue(creado.codigoVerificacion.matches(Regex("\\d{6}")))
        assertEquals(8, productos.buscarPorNombre("Cuaderno")?.stock)
        assertEquals(2, productos.buscarPorNombre("Lapiz")?.stock)
        assertEquals(2, pedidos.buscarPorCodigo(creado.codigoPedido).size)
    }

    @Test
    fun `crear rechaza cliente con pedido pendiente sin alterar stock`() {
        servicio.crear(CrearPedidoRequest("Ana", listOf(ItemPedidoRequest("Cuaderno", 1))))

        assertFailsWith<ClienteConPedidoPendienteException> {
            servicio.crear(CrearPedidoRequest("Ana", listOf(ItemPedidoRequest("Lapiz", 1))))
        }
        assertEquals(5, productos.buscarPorNombre("Lapiz")?.stock)
    }

    @Test
    fun `crear rechaza producto inexistente y stock insuficiente`() {
        assertFailsWith<ProductoNoExisteException> {
            servicio.crear(CrearPedidoRequest("Ana", listOf(ItemPedidoRequest("Regla", 1))))
        }
        assertFailsWith<StockInsuficienteException> {
            servicio.crear(CrearPedidoRequest("Luis", listOf(ItemPedidoRequest("Lapiz", 6))))
        }
        assertEquals(5, productos.buscarPorNombre("Lapiz")?.stock)
    }

    @Test
    fun `pedido solo se recibe enviado y con codigo correcto`() {
        val creado = servicio.crear(CrearPedidoRequest("Ana", listOf(ItemPedidoRequest("Cuaderno", 1))))
        assertFailsWith<EstadoInvalidoException> {
            servicio.recibir(creado.codigoPedido, creado.codigoVerificacion)
        }

        servicio.enviar(creado.codigoPedido)
        assertFailsWith<CodigoVerificacionIncorrectoException> {
            servicio.recibir(creado.codigoPedido, "000000")
        }
        val recibido = servicio.recibir(creado.codigoPedido, creado.codigoVerificacion)
        assertEquals(EstadoPedido.RECIBIDO, recibido.estado)
    }

    private class ProductosEnMemoria(productosIniciales: List<Producto>) : ProductoRepositoryPort {
        private val productos = productosIniciales.associateBy { it.nombre }.toMutableMap()

        override fun buscarPorNombre(nombre: String) = productos[nombre]

        override fun buscarPorNombreParaActualizar(nombre: String) = productos[nombre]

        override fun guardar(producto: Producto): Producto {
            productos[producto.nombre] = producto
            return producto
        }
    }
}
