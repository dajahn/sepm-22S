import { Location } from './location';
import {SmallLocation} from './smallLocation';

export interface Performance {
  id: number;
  dateTime: Date;
  location: Location;
}
export interface CreatePerformance{
  eventId: number;
  dateTime: Date;
  location: SmallLocation;
}
