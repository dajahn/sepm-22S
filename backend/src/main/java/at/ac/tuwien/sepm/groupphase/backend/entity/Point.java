package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Data
public class Point {

    @SuppressWarnings("checkstyle:membername")
    @Column(nullable = false)
    private Integer x;

    @SuppressWarnings("checkstyle:membername")
    @Column(nullable = false)
    private Integer y;
}
