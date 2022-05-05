package at.ac.tuwien.sepm.groupphase.backend.entity.embeddable;

import lombok.Getter;
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
@RequiredArgsConstructor
public class Address {

    @Column(nullable = false, length = 255)
    private String street;

    @Column(nullable = false, length = 31)
    private String zipCode;

    @Column(nullable = false, length = 127)
    private String city;

    @Column(nullable = false, length = 127)
    private String country;
}
