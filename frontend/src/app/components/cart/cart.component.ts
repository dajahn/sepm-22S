import {Component, Input, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {Cart} from '../../dtos/cart';
import {Ticket} from '../../dtos/ticket';
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
    this.loadPurchasedTickets(true);
    this.loadPurchasedTickets(false);
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
   * Loads purchased Tickets
   */
  private loadPurchasedTickets(upcoming: boolean) {
    this.purchaseService.getPurchasedTickets(upcoming).subscribe(
      (data) => {
        if (upcoming) {
          this.upcomingTickets = data;
        } else {
          this.pastTickets = data;
        }
      },
      error => {
        console.error('Error fetching purchased Tickets', error);
        this.showDanger('Sorry, something went wrong. Could not load your puchased Tickets 😔');
      }
    );
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

  showPurchaseCancelModal(ticket: Ticket) {
    const modal = this.modalService.open(CancellationModalContent, {size: 'sm', centered: true});
    modal.componentInstance.ticket = ticket;
    modal.componentInstance.cartComponent = this;
  }

  /**
   * Cancels purchased ticket
   */
  onPurchaseCancel(content: CancellationModalContent) {
    const id = content.ticket.id;
    this.purchaseService.cancelPurchasedTicket(id).subscribe({
      next: () => {
        console.log(`Successfully cancelled purchased ticket ${id}.`);
        this.upcomingTickets = this.upcomingTickets.filter((ticket) => ticket.id !== id);
        content.close('Ok click');
      },
      error: err => {
        console.error(`Error, could not cancel ticket ${id}.`, err);
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
      <p><strong>Are you sure you want to cancel your ticket?</strong></p>
      <p>This purchase will be permanently cancelled.
        <span class="text-danger">This operation can not be undone.</span>
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
  @Input() ticket: Ticket;
  @Input() cartComponent: CartComponent;

  constructor(public modal: NgbActiveModal) {
  }

  close(result?: any) {
    this.modal.close(result);
  }
}
