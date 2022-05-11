import { Event } from './event';
import { Location } from './location';

export interface Performance {
  id: number;
  event: Event;
  dateTime: Date;
  location: Location;
}
