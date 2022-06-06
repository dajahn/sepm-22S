import {Ticket} from './ticket';

export interface TicketOrder {
  id?: number;
  tickets?: Ticket[];
}
