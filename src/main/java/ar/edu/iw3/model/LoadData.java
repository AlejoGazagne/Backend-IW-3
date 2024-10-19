package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "load_data")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoadData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column()
    private long accumulatedMass;

    @Column()
    private long density;

    @Column
    private float temperature;

    @Column
    private long caudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date timestampLoad;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private Order order;
}
