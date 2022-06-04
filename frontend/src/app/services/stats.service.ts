import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Stats} from '../dtos/stats';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  private readonly baseUri: string = `${this.globals.backendUri}/stats`;

  constructor(private httpClient: HttpClient, private globals: Globals) {}

  /**
   * Get total amount of tickets, events and news
   */
  getStats(): Observable<Stats> {
    console.log(`Get stats.`);
    return this.httpClient.get<Stats>(this.baseUri);
  }
}
