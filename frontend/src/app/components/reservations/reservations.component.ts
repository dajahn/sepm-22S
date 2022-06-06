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

@Component({
  selector: 'app-reservations',
  templateUrl: './reservations.component.html',
  styleUrls: ['./reservations.component.scss']
})
export class ReservationsComponent implements OnInit {

  tickets: Ticket[];

  constructor(private cartService: CartService,
              private reservationService: ReservationService,
              private toastService: ToastService,
              public globals: Globals,
              private router: Router) {
  }

  ngOnInit(): void {
    this.loadReservations();
  }

  /**
   * Loads reservedTickets
   */
  private loadReservations() {
    this.reservationService.getReservations().subscribe({
      next: (tickets: Ticket[]) => {
        console.log(tickets);
        this.tickets = tickets;
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
   * Navigates to corresponding event and performance.
   */
  inspectEvent(eventID: number, performanceID: number) {
    this.router.navigate([`events/${eventID}/performances/${performanceID}`]).then();
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
   * Deletes all reservations and them adds them into the cart.
   */
  addToCart() {
    this.reservationService.deleteAllReservations().subscribe({
      next: _ => {
        const createTickets: CreateTicket[] = [];
        for (const item of this.tickets) {
          createTickets.push({
            performance: item.performance.id,
            type: item.sector.type,
            item: item.sector.type === SectorType.SEAT ? (item as SeatTicket).seat.id : item.sector.id
          });
        }

        this.cartService.addTicketsToCart(createTickets).subscribe({
          next: () => {
            this.showSuccess('Successfully added all items to your cart!');
            this.router.navigate(['cart']);
          },
          error: error => {
            console.log('Could not add items to cart.', error);
            this.showDanger('Unfortunately an error occurred while trying to add your items to the cart.');
          }
        });
      },
      error: err => {
        this.showDanger('Sorry, something went wrong. Could not cancel your reservations 😔 Please try again later!');
      }
    });
  }
}
