import {Sector} from './sector';
import {Seat} from './seat';

export interface SeatSector extends Sector {
  seatType: SeatType;
  seats: Seat[];
}

export enum SeatType {
  NONE = 'NONE',
  PREMIUM = 'PREMIUM',
  VIP = 'VIP'
}
