import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CreateEvent, Event} from '../dtos/event';
import {EventSearchCategory} from '../dtos/event-search-category';

@Injectable({
  providedIn: 'root'
})
export class EventService {

  private readonly eventBaseUri: string = `${this.globals.backendUri}/events`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads specific event from the backend
   *
   * @param id id of the event
   */
  getOne(id: number): Observable<Event> {
    console.log(`Load performance details for event ${id}`);
    return this.httpClient.get<Event>(`${this.eventBaseUri}/${id}`);
  }

  /**
   * Saves a new event in the backend
   *
   * @param event Event to create
   */
  save(event: CreateEvent): Observable<Event> {
    return this.httpClient.post<Event>(this.eventBaseUri, event);
  }

  getTopTenByCategory(eventSearchCategory: EventSearchCategory): Observable<Event[]> {
    console.log('get top ten events');
    let terms = new HttpParams();
    terms = terms.set('category', eventSearchCategory.category);
    return this.httpClient.get<Event[]>(this.eventBaseUri + '/top-ten-events', {params:terms});
  }

  getTopTenEventsTicketCount(eventSearchCategory: EventSearchCategory): Observable<number[]> {
    console.log('get top ten event ticket count');
    let terms = new HttpParams();
    terms = terms.set('category', eventSearchCategory.category);
    return this.httpClient.get<number[]>(this.eventBaseUri + '/count', {params: terms});
  }

}
