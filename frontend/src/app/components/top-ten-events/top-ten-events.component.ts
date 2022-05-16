import {Component, OnInit} from '@angular/core';
import {Event} from '../../dtos/event';
import {EventSearchCategory} from '../../dtos/event-search-category';
import {EventService} from '../../services/event.service';
import {Globals} from '../../global/globals';

@Component({
  selector: 'app-top-ten-events',
  templateUrl: './top-ten-events.component.html',
  styleUrls: ['./top-ten-events.component.scss']
})
export class TopTenEventsComponent implements OnInit {
  concertEvents: Event[];
  conferenceEvents: Event[];
  concertTicketCount: number[];
  conferenceTicketCount: number[];
  categories: EventSearchCategory[];

  months = ['January', 'February', 'March', 'April', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  currentMonth: string;

  constructor(private eventService: EventService, private globals: Globals) {
  }

  ngOnInit(): void {
    this.categories = [new EventSearchCategory(), new EventSearchCategory()];
    this.categories[0].category = 'CONCERT';
    this.categories[1].category = 'CONFERENCE';
    for (const item of this.categories) {
      this.getTopTenEvents(item);
      this.getTopTenEventsTicketCount(item);
    }
    this.currentMonth = this.months[new Date().getMonth() - 1];
  }

  getTopTenEvents(eventSearchCategory: EventSearchCategory) {
    if (eventSearchCategory.category === 'CONCERT') {
      this.eventService.getTopTenByCategory(eventSearchCategory).subscribe(
        (data) => {
          this.concertEvents = data;
          for (const n of this.concertEvents) {
            n.thumbnail.url = this.globals.backendUri + n.thumbnail.url;
          }
        },
        error => {
          console.error('Error searching events', error.message);
          alert('Could not load Events');
        }
      );
    } else if (eventSearchCategory.category === 'CONFERENCE') {
      this.eventService.getTopTenByCategory(eventSearchCategory).subscribe(
        (data) => {
          this.conferenceEvents = data;
          for (const n of this.conferenceEvents) {
            n.thumbnail.url = this.globals.backendUri + n.thumbnail.url;
          }
          console.log('events: ', this.conferenceEvents);
        },
        error => {
          console.error('Error searching events', error.message);
          alert('Could not load Events');
        }
      );
    }
  }

  getTopTenEventsTicketCount(eventSearchCategory: EventSearchCategory) {
    if (eventSearchCategory.category === 'CONCERT') {
      this.eventService.getTopTenEventsTicketCount(eventSearchCategory).subscribe(
        (count) => {
          this.concertTicketCount = count;
          console.log(this.concertTicketCount);
        },
        error => {
          console.error('Error getting event ticket count', error.message);
        }
      );
    } else if (eventSearchCategory.category === 'CONFERENCE') {
      this.eventService.getTopTenEventsTicketCount(eventSearchCategory).subscribe(
        (count) => {
          this.conferenceTicketCount = count;
          console.log(this.conferenceTicketCount);
        },
        error => {
          console.error('Error getting event ticket count', error.message);
        }
      );
    }
  }

  getPercent(place: number) {
    return 100 - (place * 5);
  }

  //TODO image,ticketbarwidth
}
