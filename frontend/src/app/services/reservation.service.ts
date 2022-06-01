import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Ticket} from '../dtos/ticket';
import {Observable} from 'rxjs';
import {CreateTicket} from '../dtos/create-ticket';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private readonly reservationBaseUri: string = `${this.globals.backendUri}/reservation`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  getReservations(): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(this.reservationBaseUri);
  }

  addReservations(tickets: CreateTicket[]): Observable<void> {
    return this.httpClient.post<void>(this.reservationBaseUri, tickets);
  }
}
