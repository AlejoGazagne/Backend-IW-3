package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "datos_carga")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoadData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private long masaAcumulada;

    @Column()
    private long densidad;

    @Column
    private float temperatura;

    @Column
    private long caudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date timestampCarga;

    @ManyToOne
    @JoinColumn(name = "id_orden")
    private Order orden;
}
