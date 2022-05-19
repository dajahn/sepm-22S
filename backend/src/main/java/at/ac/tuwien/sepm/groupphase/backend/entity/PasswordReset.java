package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class PasswordReset {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue
    private UUID hash;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column
    private boolean used = false;

    @Column
    @NonNull
    private LocalDateTime validUntil;

    public PasswordReset(User user) {
        this.user = user;
        this.validUntil = LocalDateTime.now().plusMinutes(5);
    }
}
