import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateEvent, Event } from '../dtos/event';
import {TopTenEvent} from '../dtos/top-ten-event';
import { EventCategory } from '../enums/event-category';

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

  /**
   * Gets an event by substring
   *
   * @param eventName
   */
  getBySubString(eventName: string, maxRecords: number): Observable<Event[]> {
    let p = new HttpParams();
    if (eventName) {
p = p.set('name', eventName);
}
    if (maxRecords) {
p = p.set('maxRecords', maxRecords);
}

    return this.httpClient.get<Event[]>(this.eventBaseUri, { params: p });
  }
  /**
   * Gets the top ten sold events by category
   *
   * @param eventCategory in which the events should be
   */
  getTopTenByCategory(eventCategory: EventCategory): Observable<TopTenEvent[]> {
    console.log('get top ten events');
    let terms = new HttpParams();
    terms = terms.set('category', eventCategory);
    return this.httpClient.get<TopTenEvent[]>(this.eventBaseUri + '/top-ten', {params: terms});
  }

}
