package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.enums.EventCategory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData.ARTIST_DESCRIPTION;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.ArtistTestData.ARTIST_NAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.BASE_URI;

public interface EventTestData {
    String EVENT_BASE_URI = BASE_URI +"/events";
    String EVENT_TEST_TITLE ="Test Event";
    String EVENT_TEST_DESCRIPTION ="Test Event description";
    Duration EVENT_TEST_DURATION = Duration.ofHours(2).plusMinutes(2);
    EventCategory EVENT_CATEGORY = EventCategory.CONCERT;
    int NUMBER_OF_PERFORMANCES = 2;
    int NUMBER_OF_ARTISTS = 2;

}
