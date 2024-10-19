package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 4, unique = true)
    private int password;

    @Column(nullable = false)
    private long preset;

    @Column(nullable = false)
    private long tare;

    @Column()
    private long finalWeight;

    @Column()
    private long state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaRecepcionInicial;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaPesajeInicial;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaInicioCarga;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaFinCarga;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaPesajeFinal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date fechaPrevistaCarga;

    @Column()
    private long valorPesoFinal;

    @Column()
    private long lastMass;

    @Column()
    private long lastDensity;

    @Column()
    private long lastTemperature;

    @Column()
    private long lastCaudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date lastTimestamp;

    @OneToOne
    @JoinColumn(name= "id_driver", nullable = false)
    private Driver driver;

    @OneToOne
    @JoinColumn(name= "id_truck", nullable = false)
    private Truck truck;

    @OneToOne
    @JoinColumn(name= "id_client", nullable = false)
    private Client client;

    @OneToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @OneToMany
    @JoinColumn(name = "id_load_data")
    private List<LoadData> loadData;

}
