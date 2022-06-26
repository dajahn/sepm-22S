import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {CreateTicket} from '../dtos/create-ticket';
import {Observable} from 'rxjs';
import {Cart} from '../dtos/cart';
import {PagedTicket, Ticket} from '../dtos/ticket';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private readonly cartBaseUri: string = `${this.globals.backendUri}/cart`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Adds multiple tickets to the cart of the currently logged-in user.
   *
   * @param tickets the tickets to be added to the logged-in users cart
   */
  addTicketsToCart(tickets: CreateTicket[]): Observable<void> {
    console.log(`Add ${tickets.length} tickets to cart.`);
    return this.httpClient.post<void>(this.cartBaseUri, tickets);
  }

  /**
   * Get the cart of the currently logged-in user.
   */
  getCart(): Observable<Cart> {
    console.log(`Get cart of logged-in user.`);
    return this.httpClient.get<Cart>(this.cartBaseUri);
  }

  /**
   * Remove ticket from the cart of the currently logged-in user.
   *
   * @param ticketId ID of the ticket which should be removed
   */
  removeTicketFromCart(ticketId: number): Observable<void> {
    console.log(`Remove ticket ${ticketId} from cart.`);
    return this.httpClient.delete<void>(`${this.cartBaseUri}/tickets/${ticketId}`);
  }

  /**
   * Get upcoming purchased Tickets of the currently logged-in user.
   */
  getUpcomingPurchasedTickets(): Observable<Ticket[]> {
    return this.httpClient.get<Ticket[]>(this.cartBaseUri + '/purchased/upcoming');
  }

  /**
   * Get past purchased Tickets of the currently logged-in user.
   */
  getPastPurchasedTickets(page: number = 0, size: number = 6): Observable<PagedTicket> {
    return this.httpClient.get<PagedTicket>(this.cartBaseUri + '/purchased/past' + '?page=' + page + '&size=' + size);
  }
}
