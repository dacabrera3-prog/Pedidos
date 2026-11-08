package diego.com.pedidos.application.exception

class ProductoNoExisteException(val nombre: String) :
    RuntimeException("No existe el producto '$nombre'")

class PedidoNoExisteException(val codigoPedido: String) :
    RuntimeException("No existe el pedido '$codigoPedido'")

class ClienteConPedidoPendienteException(val cliente: String) :
    RuntimeException("El cliente '$cliente' ya tiene un pedido pendiente")

class StockInsuficienteException(
    val producto: String,
    val disponible: Int,
    val solicitado: Int
) : RuntimeException("Stock insuficiente para '$producto': disponible $disponible, solicitado $solicitado")

class EstadoInvalidoException(message: String) : RuntimeException(message)

class CodigoVerificacionIncorrectoException :
    RuntimeException("El código de verificación es incorrecto")

class SolicitudInvalidaException(message: String) : RuntimeException(message)

class ProductoDuplicadoException(nombre: String) :
    RuntimeException("El producto '$nombre' ya existe")
