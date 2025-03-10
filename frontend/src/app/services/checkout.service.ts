import {Globals} from '../global/globals';
import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Checkout} from '../dtos/checkout';

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {

  private readonly cartBaseUri: string = `${this.globals.backendUri}/cart/checkout`;

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  /**
   * Checks out the cart of the currently logged-in user.
   */
  checkout(checkout: Checkout): Observable<void>{
    console.log(`Checkout cart of logged-in user.`);
    return this.httpClient.patch<void>(this.cartBaseUri, checkout);
  }
}
