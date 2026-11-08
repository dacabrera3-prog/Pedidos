package diego.com.pedidos.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "pedidos")
class Pedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "codigo_pedido", nullable = false, length = 32)
    var codigoPedido: String = "",

    @Column(nullable = false, length = 200)
    var cliente: String = "",

    @Column(name = "nombre_producto", nullable = false, length = 150)
    var nombreProducto: String = "",

    @Column(nullable = false)
    var cantidad: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var estado: EstadoPedido = EstadoPedido.PENDIENTE,

    @Column(name = "codigo_verificacion", nullable = false, length = 6)
    var codigoVerificacion: String = ""
)
