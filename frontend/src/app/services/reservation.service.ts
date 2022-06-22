import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Ticket} from '../dtos/ticket';
import {Observable} from 'rxjs';
import {CreateTicket} from '../dtos/create-ticket';
import {TicketOrder} from '../dtos/ticket-order';
import {Reservation} from '../dtos/reservation';

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
  getReservations(): Observable<TicketOrder[]> {
    return this.httpClient.get<TicketOrder[]>(this.reservationBaseUri);
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
   * Moves all reserved tickets into cart.
   *
   * @param tickets all tickets to be moved to cart
   */
  moveReservedTicketsToCart(tickets: Reservation[]): Observable<void>{
    const uri = `${this.reservationBaseUri}/toCart`;
    return this.httpClient.post<void>(uri, tickets);
  }
}
