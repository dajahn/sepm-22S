import {Performance} from './performance';
import {Sector} from './sector';

export interface Ticket {
  id?: number;
  performance: Performance;
  sector: Sector;
}

export interface PagedTicket {
  tickets: Ticket[];
  totalCount: number;
}
