package diego.com.pedidos.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "productos")
class Producto(
    @Id
    @Column(nullable = false, length = 150)
    var nombre: String = "",

    @Column(nullable = false, length = 1000)
    var descripcion: String = "",

    @Column(nullable = false)
    var stock: Int = 0
)
