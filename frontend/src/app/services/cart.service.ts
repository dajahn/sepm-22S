import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {CreateTicket} from '../dtos/create-ticket';
import {Observable} from 'rxjs';
import {Cart} from '../dtos/cart';

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
}
