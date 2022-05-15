import {Performance} from './performance';
import {Sector} from './sector';

export interface Ticket {
  id: number;
  performance: Performance;
  sector: Sector;
}
