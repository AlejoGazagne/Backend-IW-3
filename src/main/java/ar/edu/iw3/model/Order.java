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
        FINAL_WEIGHING,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 4, unique = true)
    private Integer password;

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
    private Date dateFinalWeighing;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date expectedChargeDate;

    @Column()
    private float finalChargeWeight;

    @Column()
    private float lastAccumulatedMass;

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

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", password=" + password +
                ", preset=" + preset +
                ", tare=" + tare +
                ", finalWeight=" + finalWeight +
                ", state=" + state +
                ", dateReceived=" + dateReceived +
                ", dateFirstWeighing=" + dateFirstWeighing +
                ", dateInitialCharge=" + dateInitialCharge +
                ", dateFinalCharge=" + dateFinalCharge +
                ", dateSecondWeighing=" + dateFinalWeighing +
                ", expectedChargeDate=" + expectedChargeDate +
                ", finalChargeWeight=" + finalChargeWeight +
                ", lastMass=" + lastAccumulatedMass +
                ", lastDensity=" + lastDensity +
                ", lastTemperature=" + lastTemperature +
                ", lastCaudal=" + lastCaudal +
                ", lastTimestamp=" + lastTimestamp +
                ", driver=" + driver +
                ", truck=" + truck +
                ", client=" + client +
                ", product=" + product +
                ", loadData=" + loadData +
                '}';
    }
}
