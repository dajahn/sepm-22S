import {SectorType} from './sector';

export class Reservation{
  orderId?: number;
  ticketId?: number;
  performance: number;
  type: SectorType;
  item: number;
}
