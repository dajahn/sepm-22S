import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Performance} from '../dtos/performance';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {

  private readonly eventBaseUri: string = `${this.globals.backendUri}/events`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Loads specific performance from the backend
   *
   * @param eventId id of the event of which the performance should be loaded
   * @param performanceId id of performance to load
   */
  getOne(eventId: number, performanceId: number): Observable<Performance> {
    console.log(`Load performance details for performance ${performanceId} from event ${eventId}`);
    return this.httpClient.get<Performance>(`${this.eventBaseUri}/${eventId}/performances/${performanceId}`);
  }

}
