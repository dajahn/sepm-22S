import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Location} from '../../../dtos/location';
import {BehaviorSubject, filter, map, share} from 'rxjs';
import {SectorType} from '../../../dtos/sector';
import {StandingSector} from '../../../dtos/standing-sector';
import {SeatSector, SeatType} from '../../../dtos/seat-sector';
import {Point} from '../../../dtos/point';
import {Seat} from '../../../dtos/seat';
import {Ticket} from '../../../dtos/ticket';
import {SeatTicket} from '../../../dtos/seat-ticket';
import {Address} from '../../../dtos/address';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html',
  styleUrls: ['./location.component.scss']
})
export class LocationComponent implements OnInit {

  static readonly GOOGLE_MAPS_API_BASE_URL = 'https://www.google.com/maps/search/?api=1';

  static readonly GRADIENTS = [
    [SeatType.VIP, 'url(\'#gradient-rainbow\')'],
    [SeatType.PREMIUM, 'url(\'#gradient-orange\')'],
    [SeatType.NONE, 'var(--text-c-lowlight)'],
  ].reduce((result, color) => {
    result[color[0]] = color[1];
    return result;
  }, {});

  @Output() seatAdd = new EventEmitter<{ sector: SeatSector; seat: Seat }>();
  @Output() standingSectorAdd = new EventEmitter<StandingSector>();
  @Output() seatRemove = new EventEmitter<{ sector: SeatSector; seat: Seat }>();

  @Input() creation = false;
  @Input() size: Point = null;

  location$ = new BehaviorSubject<Location>(null);

  private occupiedSeats = new Map<number, boolean>();
  private occupiedStandingSectors = new Map<number, number>();

  private selectedSeats = new Map<number, boolean>();
  private selectedStandingSectors = new Map<number, number>();

  private separated$ = this.location$.pipe(
    filter(location => !!location),
    map(location => location.sectors.reduce((sectors, sector) => {
      if (sector.type === SectorType.STANDING) {
        sectors.standing.push(sector as StandingSector);
      } else if (sector.type === SectorType.SEAT) {
        sectors.seat.push(sector as SeatSector);
      }
      return sectors;
    }, {standing: new Array<StandingSector>(), seat: new Array<SeatSector>()})),
    share()
  );

  private points$ = this.separated$.pipe(map(sectors => [
    ...sectors.standing.reduce<Point[]>((result, sector) => [...result, sector.point1, sector.point2], []),
    ...sectors.seat.reduce<Point[]>((result, sector) => [...result, ...sector.seats.map(seat => seat.point)], [])
  ]));

  maxX$ = this.points$.pipe(map(points => points.reduce((max, point) => point.x > max ? point.x : max, 0)));
  maxY$ = this.points$.pipe(map(points => points.reduce((max, point) => point.y > max ? point.y : max, 0)));

  standingSectorsTemp$ = this.separated$.pipe(
    map(sectors => sectors.standing),
    share(),
  );

  standingSectorPreviews$ = this.standingSectorsTemp$.pipe(map(data => data.filter(item => item.preview)), share());
  standingSectors$ = this.standingSectorsTemp$.pipe(map(data => data.filter(item => !item.preview)), share());

  seatSectors$ = this.separated$.pipe(
    map(sectors => sectors.seat),
    share(),
  );

  constructor() {
  }

  get gradients() {
    return LocationComponent.GRADIENTS;
  }

  @Input()
  set location(location: Location) {
    this.location$.next(location);
  }

  @Input()
  set occupiedTickets(tickets: Ticket[]) {
    tickets = tickets || [];
    this.occupiedSeats = this.toSeatTicketMap(tickets);
    this.occupiedStandingSectors = this.toStandingTicketMap(tickets);
  }

  @Input()
  set selectedTickets(tickets: Ticket[]) {
    tickets = tickets || [];
    this.selectedSeats = this.toSeatTicketMap(tickets);
    this.selectedStandingSectors = this.toStandingTicketMap(tickets);
  }

  ngOnInit(): void {
  }

  onSeatClick(sector: SeatSector, seat: Seat) {
    if (this.isSeatSelected(seat.id)) {
      this.seatRemove.emit({sector, seat});
    } else {
      this.seatAdd.emit({sector, seat});
    }
  }

  onStandingSectorClick(sector: StandingSector) {
    if (this.getUnavailableStandingSpots(sector.id) < sector.capacity) {
      this.standingSectorAdd.emit(sector);
    }
  }

  isSeatSelected(id: number): boolean {
    return this.selectedSeats.get(id) || false;
  }

  isSeatOccupied(id: number): boolean {
    return this.occupiedSeats.get(id) || false;
  }

  getOccupiedStandingSpots(id: number): number {
    return this.occupiedStandingSectors.get(id) || 0;
  }

  getSelectedStandingSpots(id: number): number {
    return this.selectedStandingSectors.get(id) || 0;
  }

  getUnavailableStandingSpots(id: number): number {
    return this.getOccupiedStandingSpots(id) + this.getSelectedStandingSpots(id);
  }

  getAddressString(address: Address) {
    if (!address) {
      return '';
    }
    return `${address.street}, ${address.zipCode} ${address.city}, ${address.country}`;
  }

  getAddressMapsUrl(address: Address) {
    if (!address) {
      return '';
    }
    return `${LocationComponent.GOOGLE_MAPS_API_BASE_URL}&query=${encodeURIComponent(this.getAddressString(address))}`;
  }

  private toSeatTicketMap(tickets: Ticket[]): Map<number, boolean> {
    return tickets.reduce((ticketMap, ticket) => {
      if (ticket.sector.type === SectorType.SEAT) {
        ticketMap.set((ticket as SeatTicket).seat.id, true);
      }
      return ticketMap;
    }, new Map<number, boolean>());
  }

  private toStandingTicketMap(tickets: Ticket[]): Map<number, number> {
    return tickets.reduce((ticketMap, ticket) => {
      if (ticket.sector.type === SectorType.STANDING) {
        ticketMap.set(ticket.sector.id, (ticketMap.get(ticket.sector.id) || 0) + 1);
      }
      return ticketMap;
    }, new Map<number, number>());
  }
}
