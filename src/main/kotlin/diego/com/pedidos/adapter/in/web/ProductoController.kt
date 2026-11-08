package diego.com.pedidos.adapter.`in`.web

import diego.com.pedidos.application.dto.CrearProductoRequest
import diego.com.pedidos.application.dto.ProductoResponse
import diego.com.pedidos.application.service.ProductoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/productos")
class ProductoController(private val servicio: ProductoService) {
    @PostMapping
    fun crear(@RequestBody request: CrearProductoRequest): ResponseEntity<ProductoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(request))
}
