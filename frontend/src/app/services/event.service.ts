import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Event} from '../dtos/event';

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

}
