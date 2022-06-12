import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {CreateTicket} from '../dtos/create-ticket';
import {Observable} from 'rxjs';
import {Cart} from '../dtos/cart';
import {Ticket} from '../dtos/ticket';

@Injectable({
  providedIn: 'root'
})
export class PurchaseService {

  private readonly cartBaseUri: string = `${this.globals.backendUri}/purchases`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Get purchased Tickets of the currently logged-in user.
   *
   * @param upcoming if the performance of the ticket should be in the future
   */
  getPurchasedTickets(upcoming: boolean): Observable<Ticket[]> {
    console.log(`Get purchased Tickets of logged-in user.`);
    let terms = new HttpParams();
    terms = terms.set('upcoming', upcoming);
    return this.httpClient.get<Ticket[]>(this.cartBaseUri, {params: terms});
  }

  cancelPurchasedTicket(ticketId: number) {
    console.log(`Cancel ticket with id ${ticketId}.`);
    return this.httpClient.put<Ticket[]>(this.cartBaseUri, {cancelTicket: ticketId});
  }
}
