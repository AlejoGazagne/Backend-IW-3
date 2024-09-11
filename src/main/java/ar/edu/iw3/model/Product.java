package ar.edu.iw3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(length = 100, unique = true)
	private String product;

	@Column(columnDefinition = "tinyint default 1")
	private boolean stock = true;

	private double price;

	@ManyToOne
	@JoinColumn(name = "id_category", nullable = true)
	private Category category;

	@ManyToMany(mappedBy = "products")
	private Set<Provider> providers = new HashSet<>();

	@Override
	public String toString() {
		return String.format("id=%s, product=%s, precio=%s, stock=%s ", this.getId(), this.getProduct(), this.getPrice(), this.isStock());
	}
}
