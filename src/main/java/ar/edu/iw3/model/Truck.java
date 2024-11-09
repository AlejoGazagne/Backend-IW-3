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

    @Column(unique = true)
    private String externalId;

    @Column(length = 10, nullable = false, unique = true)
    private String plate;

    @Column()
    private String description;

    @OneToMany(mappedBy = "truck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tank> tanks;

    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", plate='" + plate + '\'' +
                ", description='" + description + '\'' +
                //", tanks=" + tanks +
                '}';
    }
}
