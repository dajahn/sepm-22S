package at.ac.tuwien.sepm.groupphase.backend.entity;

public class InvoiceId {
    private int year;
    private int id;

    @Override
    public String toString() {
        return year + "-" + id;
    }
}
