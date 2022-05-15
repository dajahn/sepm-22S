import {Ticket} from './ticket';

export interface Cart {
  dateTime: string;
  tickets: Ticket[];
  validUntil: string;
}
