package at.ac.tuwien.sepm.groupphase.backend.enums;

public enum SectorType {
    SEAT(Constants.SEAT_VALUE), STANDING(Constants.STANDING_VALUE);

    SectorType(String sector) {}

    public static class Constants {
        public static final String SEAT_VALUE = "SEAT";
        public static final String STANDING_VALUE = "STANDING";
    }
}