package ar.edu.iw3.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ar.edu.iw3.auth.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alarms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Alarm {

    public enum State{
        PENDING,
        RESOLVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateOcurrence;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date dateResolved;

    @Column()
    private String description;

    @Enumerated(EnumType.STRING)
    @Column()
    private Alarm.State status;

    @Column(nullable = false)
    private float temperature;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_order", nullable = false)
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;

}