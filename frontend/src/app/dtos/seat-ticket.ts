import {Seat} from './seat';
import {Ticket} from './ticket';
import {SeatSector} from './seat-sector';

export interface SeatTicket extends Ticket {
  sector: SeatSector;
  seat: Seat;
}
