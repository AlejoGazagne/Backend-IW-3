package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tanks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private long capacidad;

    @ManyToOne
    @JoinColumn(name = "id_camion")
    private Truck camion;
}
