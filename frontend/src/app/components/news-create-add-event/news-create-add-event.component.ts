import {EventService} from './../../services/event.service';
import {OperatorFunction, Observable, switchMap, of, tap, debounceTime, distinctUntilChanged, catchError} from 'rxjs';
import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Event} from 'src/app/dtos/event';

@Component({
  selector: 'app-news-create-add-event',
  templateUrl: './news-create-add-event.component.html',
  styleUrls: ['./news-create-add-event.component.scss']
})
export class NewsCreateAddEventComponent implements OnInit {
  @Output() updateEventEmitter = new EventEmitter<Event>();

  public searchEventFailed = false;
  public eventError = false;
  public event: Event;


  constructor(private eventService: EventService) {
  }

  ngOnInit(): void {
  }

  delete() {
    this.event = null;
    this.updateEventEmitter.emit(null);
  }

  updateEvent(e: any) {
    this.event = e;
    if (typeof this.event !== 'object' || !this.event) {
      this.eventError = true;
    } else {
      this.updateEventEmitter.emit(this.event);
      this.eventError = false;
    }

  }

  resultFormatEventListValue(e: Event) {
    return e.name;
  }

  inputFormatEventListValue(value: Event) {
    if (value.name) {
      return value.name;
    }
    return value;
  }

  searchEvent: OperatorFunction<string, readonly string[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(name => this.eventService.getBySubString(name, 5).pipe(
        tap(() => this.searchEventFailed = false),
        catchError(() => {
          this.searchEventFailed = true;
          return of([]);
        }))),
      catchError(() => {
        this.searchEventFailed = true;
        return of([]);
      })
    );
}
