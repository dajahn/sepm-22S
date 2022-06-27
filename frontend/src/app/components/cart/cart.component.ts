import {Component, Input, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Cart} from '../../dtos/cart';
import {PagedTicket, Ticket} from '../../dtos/ticket';
import {SeatTicket} from '../../dtos/seat-ticket';
import {CheckoutService} from '../../services/checkout.service';
import {ToastService} from '../../services/toast-service.service';
import {Globals} from '../../global/globals';
import {Router} from '@angular/router';
import {PurchaseService} from '../../services/purchase.service';
import {ErrorMessageParser} from '../../utils/error-message-parser';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {

  cart: Cart;
  upcomingTickets: Ticket[];
  pastTickets: Ticket[];

  selectedPurchasedTickets: number[] = [];

  pageSize = 6;
  pastPurchasedPage = 1;
  totalAllPastPurchasedPage = 0;

  constructor(
    private cartService: CartService,
    private checkoutService: CheckoutService,
    private purchaseService: PurchaseService,
    private toastService: ToastService,
    public globals: Globals,
    private router: Router,
    private modalService: NgbModal) {
  }

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
    this.purchaseService.getUpcomingPurchasedTickets().subscribe({
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
    this.purchaseService.getPastPurchasedTickets(this.pastPurchasedPage - 1, this.pageSize).subscribe({
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
  onCartRemove(id: number) {
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

  showPurchaseCancelModal() {
    const modal = this.modalService.open(CancellationModalContent, {size: 'sm', centered: true});
    modal.componentInstance.tickets = [...this.selectedPurchasedTickets];
    modal.componentInstance.cartComponent = this;
  }

  /**
   * Selects a purchased ticket, or deselects it, if it has already been selected
   *
   * @param ticketId ID of the ticket to be selected
   */
  selectPurchasedTicket(ticketId: number) {
    if (this.isPurchasedTicketSelected(ticketId)) {
      this.selectedPurchasedTickets = this.selectedPurchasedTickets.filter(id => id !== ticketId);
    } else {
      this.selectedPurchasedTickets.push(ticketId);
    }
  }

  /**
   * Checks if a purchased ticket has been selected
   *
   * @param ticketId ID of the ticket to be checked
   */
  isPurchasedTicketSelected(ticketId: number): boolean {
    return this.selectedPurchasedTickets.some(id => id === ticketId);
  }

  /**
   * Checks if there is no purchased ticket selected
   */
  areNoPurchasedTicketsSelected(): boolean {
    return this.selectedPurchasedTickets.length === 0;
  }

  /**
   * Cancels purchased ticket
   */
  onPurchaseCancel(content: CancellationModalContent) {
    const tickets = content.tickets;
    this.purchaseService.cancelPurchasedTickets(tickets).subscribe({
      next: () => {
        console.log(`Successfully cancelled purchased tickets ${tickets}.`);
        this.upcomingTickets = this.upcomingTickets.filter((ticket) => !tickets.some(id => ticket.id === id));
        this.selectedPurchasedTickets = [];
        content.close('Ok click');
      },
      error: err => {
        console.error(`Error, could not cancel tickets ${tickets}.`, err);
        this.showDanger(`Sorry, ticket could not be cancelled 😔\n${ErrorMessageParser.parseResponseToErrorMessage(err)}`);
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

@Component({
  selector: 'app-cancellation-modal-content',
  template: `
    <div class="modal-header">
      <h4 class="modal-title" id="modal-title">Purchase Cancellation</h4>
      <button type="button" ngbAutofocus class="btn-close" aria-label="Close button" aria-describedby="modal-title"
              (click)="modal.dismiss('Cross click')"></button>
    </div>
    <div class="modal-body">
      <p><strong>Are you sure you want to cancel the selected tickets?</strong></p>
      <p>This purchases will be permanently cancelled.
        <span class="text-danger">This operation cannot be undone.</span>
      </p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-outline-secondary" (click)="modal.dismiss('cancel click')">Cancel</button>
      <button type="button" class="btn btn-danger" (click)="cartComponent.onPurchaseCancel(this)">Ok
      </button>
    </div>
  `
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class CancellationModalContent {
  @Input() tickets: number[];
  @Input() cartComponent: CartComponent;

  constructor(public modal: NgbActiveModal) {
  }

  close(result?: any) {
    this.modal.close(result);
  }
}
