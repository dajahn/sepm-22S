import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {Observable} from 'rxjs';
import {PagedTicket, Ticket} from '../dtos/ticket';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private readonly cartBaseUri: string = `${this.globals.backendUri}/purchases`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
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

  cancelPurchasedTickets(ticketIds: number[]) {
    console.log(`Cancel ticket with ids ${ticketIds}.`);
    return this.httpClient.put<Ticket[]>(this.cartBaseUri, {cancelTickets: ticketIds});
  }
}
