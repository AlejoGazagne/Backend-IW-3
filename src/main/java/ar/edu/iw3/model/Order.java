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
    public enum State{
        RECEIVED,
        FIRST_WEIGHING,
        CHARGED,
        SECOND_WEIGHING,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 4, unique = true)
    private int password;

    @Column(nullable = false)
    private float preset;

    @Column()
    private float tare;

    @Column()
    private float finalWeight;

    @Column(nullable = false)
    private State state;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateReceived;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateFirstWeighing;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateInitialCharge;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateFinalCharge;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateSecondWeighing;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date expectedChargeDate;

    @Column()
    private float finalChargeWeight;

    @Column()
    private float lastMass;

    @Column()
    private float lastDensity;

    @Column()
    private float lastTemperature;

    @Column()
    private float lastCaudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date lastTimestamp;

    @ManyToOne
    @JoinColumn(name= "id_driver", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name= "id_truck", nullable = false)
    private Truck truck;

    @ManyToOne
    @JoinColumn(name= "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "order")
    private List<LoadData> loadData;

}
