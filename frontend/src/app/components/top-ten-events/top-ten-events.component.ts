import {Component, OnInit} from '@angular/core';
import {EventCategory} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {Globals} from '../../global/globals';
import {ToastService} from '../../services/toast-service.service';

@Component({
  selector: 'app-top-ten-events',
  templateUrl: './top-ten-events.component.html',
  styleUrls: ['./top-ten-events.component.scss']
})
export class TopTenEventsComponent implements OnInit {

  categories = EventCategory;
  categoriesValues = [];
  topTenEvents = [];

  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  currentMonth: string;

  constructor(private eventService: EventService, private globals: Globals, private toastService: ToastService) {
    this.categoriesValues = Object.values(this.categories);
  }

  ngOnInit(): void {
    this.currentMonth = this.months[new Date().getMonth() - 1];
    // eslint-disable-next-line guard-for-in
    for (const category in this.categoriesValues) {
      this.getTopTenEvents(this.categoriesValues[category]);
    }
  }

  /**
   * Gets the Top Ten Events in one category
   */
  getTopTenEvents(eventCategory: EventCategory) {
    this.eventService.getTopTenByCategory(eventCategory).subscribe(
      (data) => {
        this.topTenEvents[eventCategory] = data;
        console.log(data);
        for (const n of this.topTenEvents[eventCategory]) {
          if (n.thumbnail !== null) {
            n.thumbnail.url = this.globals.backendUri + n.thumbnail.url;
          }
        }
      },
      error => {
        console.log('Error getting top ten events', error.message);
        this.showDanger('Sorry, something went wrong. Could not load the top ten concerts 😔');
      }
    );
  }

  /**
   * Displays message on a failure.
   */
  showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger', delay: 5000});
  }

  /**
   * Calculates Percent for Bar width
   */
  getPercent(place: number, cat: EventCategory) {
    const mostTickets = this.topTenEvents[cat][0].ticketCount;
    const onePercent = 100 / mostTickets;
    let percent = this.topTenEvents[cat][place].ticketCount * onePercent;
    if (percent < 40) {
      percent = 40;
    }
    return percent;
  }
}
