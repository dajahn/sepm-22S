import {Location} from './location';
import {Event} from './event';

export interface Performance {
  id?: number;
  dateTime: Date;
  location: Location;
  event?: Event;
}
