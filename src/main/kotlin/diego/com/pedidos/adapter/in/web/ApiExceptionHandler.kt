package diego.com.pedidos.adapter.`in`.web

import diego.com.pedidos.application.exception.ClienteConPedidoPendienteException
import diego.com.pedidos.application.exception.CodigoVerificacionIncorrectoException
import diego.com.pedidos.application.exception.EstadoInvalidoException
import diego.com.pedidos.application.exception.PedidoNoExisteException
import diego.com.pedidos.application.exception.ProductoDuplicadoException
import diego.com.pedidos.application.exception.ProductoNoExisteException
import diego.com.pedidos.application.exception.SolicitudInvalidaException
import diego.com.pedidos.application.exception.StockInsuficienteException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiError(val mensaje: String)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ProductoNoExisteException::class, PedidoNoExisteException::class)
    fun noEncontrado(exception: RuntimeException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError(exception.message ?: "No encontrado"))

    @ExceptionHandler(
        ClienteConPedidoPendienteException::class,
        StockInsuficienteException::class,
        EstadoInvalidoException::class,
        ProductoDuplicadoException::class
    )
    fun conflicto(exception: RuntimeException) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError(exception.message ?: "Conflicto"))

    @ExceptionHandler(CodigoVerificacionIncorrectoException::class, SolicitudInvalidaException::class)
    fun solicitudIncorrecta(exception: RuntimeException) =
        ResponseEntity.badRequest().body(ApiError(exception.message ?: "Solicitud incorrecta"))
}
