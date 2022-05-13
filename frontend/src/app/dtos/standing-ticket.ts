import {Sector} from './sector';
import {StandingSector} from './standing-sector';

export interface StandingTicket extends Sector {
  sector: StandingSector;
}
