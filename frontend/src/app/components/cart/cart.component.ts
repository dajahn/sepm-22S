import { Component, OnInit } from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Cart} from '../../dtos/cart';
import {Ticket} from '../../dtos/ticket';
import {SeatTicket} from '../../dtos/seat-ticket';
import {CheckoutService} from '../../services/checkout.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {

  cart: Cart;

  constructor(private cartService: CartService, private checkoutService: CheckoutService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  /**
   * Loads cart order
   */
  private loadCart() {
    this.cartService.getCart().subscribe({
      next: (cart: Cart) => {
        console.log(cart);
        this.cart = cart;
      },
      error: err => {
        console.error('Error fetching cart', err);
      }
    });
  }

  /**
   * Converts a general ticket into a SeatTicket
   *
   * @param ticket to convert
   */
  toSeatTicket(ticket: Ticket): SeatTicket {
    return ticket as SeatTicket;
  }

  /**
   * Calculates the total price of all tickets combined
   */
  calculateTotalSum(): number{
    return this.cart?.tickets.reduce((accumulator, current) => accumulator + current?.sector.price, 0);
  }

  /**
   * Removes ticket from cart
   */
  onRemove(id: number) {
    this.cart.tickets.forEach((ticket,index)=>{
      if(ticket.id === id) {
        delete this.cart.tickets[index];
        console.log('Removed '+id);
      }
    });
  }

  /**
   * Checks out the cart of the currently logged-in user.
   */
  checkout() {
    this.checkoutService.checkout().subscribe({
      next: () => {
        console.log('Successfully checked out cart!');
        this.cart.tickets = [];
      },
      error: err => {
        console.error('Error checking out cart', err);
      }
    });
  }
}
