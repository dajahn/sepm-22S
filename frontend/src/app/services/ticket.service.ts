import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Ticket} from '../dtos/ticket';

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  private readonly eventBaseUri: string = `${this.globals.backendUri}/events`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads all valid tickets or reservations for a performance
   *
   * @param eventId id of the event of which the performance is a part of
   * @param performanceId id of performance from which the tickets should be loaded
   */
  getTicketsByEventAndPerformance(eventId: number, performanceId: number): Observable<Ticket[]> {
    console.log(`Load performance details for performance ${performanceId} from event ${eventId}`);
    return this.httpClient.get<Ticket[]>(`${this.eventBaseUri}/${eventId}/performances/${performanceId}/tickets`);
  }

}
