import {StandingSector} from './standing-sector';
import {Ticket} from './ticket';

export interface StandingTicket extends Ticket {
  sector: StandingSector;
}
