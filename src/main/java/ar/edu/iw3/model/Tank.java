package ar.edu.iw3.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

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

    @Column(nullable = false)
    private long capacity;

    @ManyToOne
    @JoinColumn(name = "id_truck", nullable = false)
    @JsonBackReference
    private Truck truck;

    @Override
    public String toString() {
        return "Tank{" +
                "id=" + id +
                ", capacity=" + capacity +
                //", truck=" + (truck != null ? truck.getId() : null) +
                '}';
    }
}
