import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {Performance} from '../dtos/performance';
import {PerformanceSearchParams} from "../dtos/performanceSearchParams";

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

  findAllPerformancesBy(searchTerms: PerformanceSearchParams): Observable<Performance[]> {
    console.log(`Find all performances with search terms: ${searchTerms}`);
    let terms = new HttpParams();
    if(searchTerms.locationName) {
      terms = terms.set('locationName', searchTerms.locationName);
    }
    if(searchTerms.eventName) {
      terms = terms.set('eventName', searchTerms.eventName);
    }
    if(searchTerms.toDate) {
      terms = terms.set('toDate', searchTerms.toDate.toString());
    }
    if(searchTerms.fromDate) {
      terms = terms.set('fromDate', searchTerms.fromDate.toString());
    }
    if(searchTerms.price) {
      terms = terms.set('price', searchTerms.price);
    }
    return this.httpClient.get<Performance[]>(this.eventBaseUri + '/performances/search', {params: terms});
  }

}
