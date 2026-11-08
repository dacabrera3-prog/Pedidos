package diego.com.pedidos.adapter.`in`.web

import diego.com.pedidos.application.dto.CrearPedidoRequest
import diego.com.pedidos.application.dto.PedidoCreadoResponse
import diego.com.pedidos.application.dto.PedidoResponse
import diego.com.pedidos.application.dto.RecibirPedidoRequest
import diego.com.pedidos.application.service.PedidoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pedidos")
class PedidoController(private val servicio: PedidoService) {
    @PostMapping
    fun crear(@RequestBody request: CrearPedidoRequest): ResponseEntity<PedidoCreadoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(request))

    @PatchMapping("/{codigo}/enviar")
    fun enviar(@PathVariable codigo: String): PedidoResponse = servicio.enviar(codigo)

    @PatchMapping("/{codigo}/recibir")
    fun recibir(
        @PathVariable codigo: String,
        @RequestBody request: RecibirPedidoRequest
    ): PedidoResponse = servicio.recibir(codigo, request.codigoVerificacion)
}
