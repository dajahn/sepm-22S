import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Event} from '../dtos/event';
const baseUri = new Globals().backendUri + '/events';
@Injectable({
  providedIn: 'root'
})
export class EventService {
  constructor(private http: HttpClient) {
  }

  save(event: Event): Observable<Event> {
    console.log(baseUri);
    return this.http.post<Event>(baseUri, event);
  }
}
