package ar.edu.iw3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "providers")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, unique = true)
    private String provider;

    @ManyToMany
    @JoinTable(
            name = "provider_product",
            joinColumns = @JoinColumn(name = "id_provider"),
            inverseJoinColumns = @JoinColumn(name = "id_product")
    )
    @JsonIgnoreProperties("providers")
    private List<Product> products = new ArrayList<>();
}
