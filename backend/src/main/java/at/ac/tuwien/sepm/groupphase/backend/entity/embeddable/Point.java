package at.ac.tuwien.sepm.groupphase.backend.entity.embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * This class is used to represent a point inside a graphical representation. It can be embedded in entities.
 */
@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Point {

    @SuppressWarnings("checkstyle:membername")
    @Column(nullable = false)
    @NonNull
    private Integer x;

    @SuppressWarnings("checkstyle:membername")
    @Column(nullable = false)
    @NonNull
    private Integer y;
}
