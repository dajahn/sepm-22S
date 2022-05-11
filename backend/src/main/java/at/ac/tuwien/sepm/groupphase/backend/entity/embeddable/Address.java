package at.ac.tuwien.sepm.groupphase.backend.entity.embeddable;

import at.ac.tuwien.sepm.groupphase.backend.enums.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This class is used to represent an address. It can be embedded in entities.
 */
@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Address {

    @Column(nullable = false, length = 255)
    @NonNull
    private String street;

    @Column(nullable = false, length = 31)
    @NonNull
    private String zipCode;

    @Column(nullable = false, length = 127)
    @NonNull
    private String city;

    @Column(nullable = false)
    @NonNull
    private Country country;
}
