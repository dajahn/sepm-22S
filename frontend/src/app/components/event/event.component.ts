import {Component, OnInit, ViewChild} from '@angular/core';
import {EventService} from 'src/app/services/event.service';
import {ActivatedRoute, Router} from '@angular/router';
import {catchError, EMPTY, filter, map, Observable, share, switchMap, tap} from 'rxjs';
import {PerformanceService} from '../../services/performance.service';
import {StandingSector} from '../../dtos/standing-sector';
import {Ticket} from '../../dtos/ticket';
import {SeatTicket} from 'src/app/dtos/seat-ticket';
import {StandingTicket} from '../../dtos/standing-ticket';
import {Performance} from '../../dtos/performance';
import {TicketService} from '../../services/ticket.service';
import {CartService} from '../../services/cart.service';
import {ToastService} from '../../services/toast-service.service';
import {SectorType} from '../../dtos/sector';
import {Globals} from '../../global/globals';
import {ReservationService} from '../../services/reservation.service';

@Component({
  selector: 'app-performance',
  templateUrl: './event.component.html',
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {

  @ViewChild('performanceSelect') performanceSelect: HTMLSelectElement;

  readonly event$ = this.route.params.pipe(
    map(params => params.eventId),
    filter(id => id != null),
    share(),
    switchMap(id => this.eventService.getOne(id)),
    catchError((err) => {
      console.log('Could not load event.', err);
      this.showDanger('Unfortunately we were unable to load the requested event.');
      return EMPTY;
    }),
    share(),
  );

  readonly thumbnail$ = this.event$.pipe(
    map(event => event?.thumbnail?.url ? this.globals.backendUri + event?.thumbnail?.url : ''),
  );

  readonly performanceId$ = this.event$.pipe(
    switchMap(event => this.route.params.pipe(
        map(({performanceId}) => {
          if (!performanceId) {
            performanceId = this.getFirstFuturePerformance(event?.performances)?.id;
            this.router.navigate(['performances', performanceId], {relativeTo: this.route});
          }
          return performanceId;
        })
      )
    ),
    share()
  );

  readonly performance$: Observable<Performance> = this.event$.pipe(
    switchMap(event => this.performanceId$.pipe(
        map(performanceId => ({eventId: event.id, performanceId}))
      )
    ),
    share(),
    switchMap(params => this.performanceService.getOne(params.eventId, params.performanceId)),
    catchError((err) => {
      console.log('Could not load performance.', err);
      this.showDanger('Unfortunately we were unable to load the requested performance.');
      return EMPTY;
    }),
    tap(performance => {
      this.performance = performance;
      this.selectedTickets = [];
    }),
    share(),
  );

  private performance: Performance;

  occupiedTickets = this.performance$.pipe(
    switchMap(performance => this.ticketService.getTicketsByEventAndPerformance(performance.event.id, performance.id)),
    catchError((err) => {
      console.log('Could not load tickets.', err);
      this.showDanger('Unfortunately we were unable to load some information about the requested performance.');
      return EMPTY;
    }),
  );

  selectedTickets: Ticket[] = [];

  constructor(
    private readonly eventService: EventService,
    private readonly performanceService: PerformanceService,
    private readonly ticketService: TicketService,
    private readonly cartService: CartService,
    private readonly toastService: ToastService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly globals: Globals,
    private readonly reservationService: ReservationService
  ) {
  }

  get sum() {
    return this.selectedTickets.reduce((sum, ticket) => sum + ticket.sector.price, 0);
  }

  ngOnInit(): void {
  }

  getFirstFuturePerformance(performances: Performance[]): Performance {
    const futurePerformances = this.getFuturePerformances(performances);
    if (futurePerformances.length > 0) {
      return futurePerformances[0];
    }
    return null;
  }

  getFuturePerformances(performances: Performance[]): Performance[] {
    if (!performances) {
      return [];
    }
    return performances.filter(performance => Date.parse(performance.dateTime).valueOf() > Date.now());
  }

  onSeatAdd({sector, seat}) {
    this.selectedTickets.push({
      performance: this.performance,
      sector,
      seat
    } as SeatTicket);
    this.selectedTickets = [...this.selectedTickets];
  }

  onSeatRemove({sector, seat}) {
    this.selectedTickets =
      this.selectedTickets.filter(ticket => ticket.sector.id !== sector.id || (ticket as SeatTicket).seat.id !== seat.id);
  }

  onStandingSectorAdd(sector: StandingSector) {
    this.selectedTickets.push({
      performance: this.performance,
      sector
    } as StandingTicket);
    this.selectedTickets = [...this.selectedTickets];
  }

  onStandingSectorRemove(sector: StandingSector) {
    const index = this.selectedTickets.findIndex(ticket => ticket.sector.id === sector.id);
    if (index >= 0) {
      this.selectedTickets.splice(index, 1);
      this.selectedTickets = [...this.selectedTickets];
    }
  }

  toSeatTicket(ticket: Ticket): SeatTicket {
    return ticket as SeatTicket;
  }

  toStandingTicket(ticket: Ticket): StandingTicket {
    return ticket as StandingTicket;
  }

  addToCart() {
    this.cartService.addTicketsToCart(this.selectedTickets.map(ticket => ({
      performance: ticket.performance.id,
      type: ticket.sector.type,
      item: ticket.sector.type === SectorType.SEAT ? (ticket as SeatTicket).seat.id : ticket.sector.id
    }))).subscribe({
      next: () => {
        this.showSuccess('Successfully added items to your cart!');
        this.router.navigate(['cart']);
      },
      error: error => {
        console.log('Could not add items to cart.', error);
        this.showDanger('Unfortunately an error occurred while trying to add you items to the cart.');
      }
    });
  }

  showSuccess(msg: string) {
    this.toastService.show(msg, {classname: 'bg-success', delay: 3000});
  }

  showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger', delay: 5000});
  }

  onPerformanceChange(id: number) {
    if (!!id) {
      this.router.navigate(['..', id], {relativeTo: this.route});
    }
  }

  reserveTickets() {
    this.reservationService.addReservations(this.selectedTickets.map(ticket => ({
      performance: ticket.performance.id,
      type: ticket.sector.type,
      item: ticket.sector.type === SectorType.SEAT ? (ticket as SeatTicket).seat.id : ticket.sector.id
    }))).subscribe({
      next: () => {
        this.showSuccess('Successfully reserved tickets!');
        this.router.navigate(['reservations']);
      },
      error: error => {
        console.log('Could not reserve tickets.', error);
        this.showDanger('Unfortunately an error occurred while trying to reserve your items.');
      }
    });
  }
}
