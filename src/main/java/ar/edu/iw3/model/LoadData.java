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

    @Column(nullable = false)
    private float accumulatedMass;

    @Column(nullable = false)
    private float density;

    @Column(nullable = false)
    private float temperature;

    @Column(nullable = false)
    private float caudal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestampLoad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_order", nullable = false)
    private Order order;
}
