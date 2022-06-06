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

  /**
   * Fetches all reservations of current user.
   */
  getReservations(): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(this.reservationBaseUri);
  }

  /**
   * Adds new reservation for current user.
   */
  addReservations(tickets: CreateTicket[]): Observable<void> {
    return this.httpClient.post<void>(this.reservationBaseUri, tickets);
  }

  /**
   * Deletes a single reservation of current user.
   */
  deleteReservation(ticketId: number): Observable<void> {
    const uri = `${this.reservationBaseUri}/${ticketId}`;
    return this.httpClient.delete<void>(uri);
  }

  /**
   * Deletes all reservations of current user.
   */
  deleteAllReservations(): Observable<void> {
    return this.httpClient.delete<void>(this.reservationBaseUri);
  }
}
