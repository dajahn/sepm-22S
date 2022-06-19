package at.ac.tuwien.sepm.groupphase.backend.basetest;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.BASE_URI;

public interface UserTestData {
    String USER_BASE_URI = BASE_URI + "/users";
    String USER_FIRST_NAME = "New User First";
    String USER_LAST_NAME = "New User Last";
    String USER_EMAIL ="ul9@example.com";
    String USER_PASSWORD="password123";

    String ADMIN_EMAIL = "admin@example.com";

    String ADMIN_PASSWORD="password";
}
