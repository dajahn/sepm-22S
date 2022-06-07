import {Component, OnInit} from '@angular/core';
import {CartService} from '../../services/cart.service';
import {ToastService} from '../../services/toast-service.service';
import {Globals} from '../../global/globals';
import {Router} from '@angular/router';
import {ReservationService} from '../../services/reservation.service';
import {Ticket} from '../../dtos/ticket';
import {SeatTicket} from '../../dtos/seat-ticket';
import {CreateTicket} from '../../dtos/create-ticket';
import {SectorType} from '../../dtos/sector';
import {TicketOrder} from '../../dtos/ticket-order';
import {Reservation} from '../../dtos/reservation';

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.scss']
})
export class ReservationsComponent implements OnInit {

  orders: TicketOrder[];
  selectedReservations: Reservation[];
  ticketSum = 0;

  constructor(private cartService: CartService,
              private reservationService: ReservationService,
              private toastService: ToastService,
              public globals: Globals,
              private router: Router) {
  }

  ngOnInit(): void {
    this.selectedReservations = [];
    this.loadReservations();
  }

  /**
   * Loads reserved Tickets
   */
  private loadReservations() {
    this.reservationService.getReservations().subscribe({
      next: (orders: TicketOrder[]) => {
        this.orders = orders;
        this.ticketSum = 0;
        this.orders.forEach(x => {
          this.ticketSum += x.tickets?.length;
        });
      },
      error: err => {
        console.error('Error fetching tickets', err);
        this.showDanger('Sorry, something went wrong. Could not load your reserved tickets 😔 Please try again later!');
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
   * Removes reservation for ticket
   */
  onRemove(id: number) {
    this.reservationService.deleteReservation(id).subscribe({
      next: _ => {
        this.loadReservations();
      },
      error: err => {
        console.error(err);
        this.showDanger('Sorry, something went wrong. Could not cancel your reservation 😔 Please try again later!');
      }
    });
  }

  /**
   *
   * Selects or un-selects a ticket if clicked
   */
  ticketClicked(orderID: number, ticket: Ticket, index: number) {
    let tmpInd = 0;
    for (const item of this.orders) {
      if (item.id === orderID) {
        break;
      }
      tmpInd += item.tickets?.length;
    }
    tmpInd += index;
    const tmpItem = this.selectedReservations[tmpInd];
    if (tmpItem === null || tmpItem === undefined) {
      this.selectedReservations[tmpInd] = {
        orderId: orderID,
        ticketId: ticket.id,
        performance: ticket.performance.id,
        type: ticket.sector.type,
        item: ticket.sector.type === SectorType.SEAT ? (ticket as SeatTicket).seat.id : ticket.sector.id
      };
    } else {
      delete this.selectedReservations[tmpInd];
    }
  }

  /**
   * Returns true, if no ticket is selected
   */
  areSelectedTicketsEmpty(){
    return this.selectedReservations.filter(r => r !== null && r !== undefined).length ===0;
  }

  /**
   * Returns true if a specific ticket is selected.
   *
   * @param orderID ID of order containing the ticket
   * @param index of the ticket inside of the order
   */
  ticketIsSelected(orderID: number, index: number) {
    let tmpInd = 0;
    for (const item of this.orders) {
      if (item.id === orderID) {
        break;
      }
      tmpInd += item.tickets?.length;
    }
    tmpInd += index;
    const tmpItem = this.selectedReservations[tmpInd];
    return !(tmpItem === null || tmpItem === undefined);
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
   * Deletes selected reservations and adds them to cart.
   */
  addToCart() {
    this.selectedReservations = this.selectedReservations.filter(r => r !== null && r !== undefined);
    if(this.selectedReservations.length === 0 ){
      this.showDanger('There are no tickets selected.');
      return;
    }
    this.reservationService.moveReservedTicketsToCart(this.selectedReservations).subscribe({
      next: _ => {
        this.showSuccess('Successfully added all items to your cart!');
        this.router.navigate(['cart']);
      },
      error: error => {
        console.log('Could not add items to cart.', error);
        this.showDanger('Unfortunately an error occurred while trying to add your items to the cart.');
      }
    });
  }
}
