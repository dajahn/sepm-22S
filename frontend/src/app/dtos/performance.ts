import { Location } from './location';
import {SmallLocation} from './smallLocation';
import {Event} from './event';

export interface Performance {
  id: number;
  dateTime: Date;
  location: Location;
  event: Event;
}
export interface CreatePerformance{
  eventId: number;
  dateTime: Date;
  location: SmallLocation;
}
