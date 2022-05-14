import { Injectable } from '@angular/core';
import {Globals} from '../global/globals';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CreateEvent, Event} from '../dtos/event';
const baseUri = new Globals().backendUri + '/events';
@Injectable({
  providedIn: 'root'
})
export class EventService {
  constructor(private http: HttpClient) {
  }

  save(event: CreateEvent): Observable<Event> {
    console.log(baseUri);
    return this.http.post<Event>(baseUri, event);
  }
}
