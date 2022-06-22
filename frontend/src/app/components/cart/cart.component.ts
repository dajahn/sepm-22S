import {Component, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Cart} from '../../dtos/cart';
import {PagedTicket, Ticket} from '../../dtos/ticket';
import {SeatTicket} from '../../dtos/seat-ticket';
import {CheckoutService} from '../../services/checkout.service';
import {ToastService} from '../../services/toast-service.service';
import {Globals} from '../../global/globals';
import {Router} from '@angular/router';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {

  cart: Cart;
  upcomingTickets: Ticket[];
  pastTickets: Ticket[];

  pageSize = 6;
  pastPurchasedPage = 1;
  totalAllPastPurchasedPage = 0;

  constructor(
    private cartService: CartService,
    private checkoutService: CheckoutService,
    private toastService: ToastService,
    public globals: Globals,
    private router: Router) {}

  ngOnInit(): void {
    this.loadCart();
    this.loadUpcomingPurchasedTickets();
    this.loadPastPurchasedTickets();
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
        this.showDanger('Sorry, something went wrong. Could not load the cart 😔 Please try again later!');
      }
    });
  }

  /**
   * Loads upcoming purchased Tickets
   */
  private loadUpcomingPurchasedTickets() {
    this.cartService.getUpcomingPurchasedTickets().subscribe({
      next: (tickets: Ticket[]) => {
        console.log(tickets);
        this.upcomingTickets = tickets;
      },
      error: err => {
        console.error('Error fetching upcoming purchased Tickets', err);
        this.showDanger('Sorry, something went wrong. Could not load your purchased Tickets 😔');
      }
    });
  }

  private loadPastPurchasedTickets() {
    this.cartService.getPastPurchasedTickets(this.pastPurchasedPage - 1, this.pageSize).subscribe({
      next: (pagedTicket: PagedTicket) => {
        console.log(pagedTicket);
        this.totalAllPastPurchasedPage = pagedTicket.totalCount;
        this.pastTickets = pagedTicket.tickets;
      },
      error: err => {
        console.error('Error fetching past purchased Tickets', err);
        this.showDanger('Sorry, something went wrong. Could not load your purchased Tickets 😔');
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
  calculateTotalSum(): number {
    return this.cart?.tickets.reduce((accumulator, current) => accumulator + current?.sector.price, 0);
  }

  /**
   * Removes ticket from cart
   */
  onRemove(id: number) {
    this.cartService.removeTicketFromCart(id).subscribe({
      next: () => {
        console.log(`Successfully removed ticket ${id} from cart.`);
        this.cart.tickets = this.cart.tickets.filter((ticket) => ticket.id !== id);
      },
      error: err => {
        console.error(`Error, could not remove ticket ${id} from cart.`, err);
        this.showDanger(`Sorry, ticket could not be removed from the cart 😔 Please try again later!`);
      }
    });
  }

  /**
   * Checks out the cart of the currently logged-in user.
   */
  checkout() {
    this.router.navigate(['/checkout']).then();
  }

  /**
   * Displays message on a success.
   */
  showSuccess(msg: string) {
    this.toastService.show(msg, {classname: 'bg-success', delay: 3000});
  }

  /**
   * Displays message on a failure.
   */
  showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger', delay: 5000});
  }

  /**
   *
   * Navigates to corresponding event and performance.
   */
  inspect(eventID: number, performanceID: number) {
    this.router.navigate([`events/${eventID}/performances/${performanceID}`]).then();
  }

  handlePageChangePastPurchasedPage() {
    this.loadPastPurchasedTickets();
  }
}
