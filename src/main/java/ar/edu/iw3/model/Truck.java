package ar.edu.iw3.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Truck entity")
public class Truck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Truck id")
    private long id;

    @Column(length = 10, nullable = false, unique = true)
    @Schema(description = "Truck plate")
    private String plate;

    @Column()
    @Schema(description = "Truck description")
    private String description;

    @OneToMany
    @JoinColumn(name = "id_tank", nullable = false)
    @Schema(description = "Truck tanks")
    private List<Tank> tanks;

    @Override
    public String toString() {
        return "Truck{" +
                "id=" + id +
                ", plate='" + plate + '\'' +
                ", description='" + description + '\'' +
                ", tanks=" + tanks +
                '}';
    }
}
