package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "trucks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private String patente;

    @Column()
    private String descripcion;

    @OneToMany
    @JoinColumn(name = "id_cisterna", nullable = false)
    private List<Tank> cisterna;
}
