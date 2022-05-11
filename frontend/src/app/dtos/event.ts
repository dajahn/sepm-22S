import {Artist} from './artist';

export interface Event {
  id: number;
  name: string;
  description: string;
  duration: number;
  artists: Artist[];
  category: EventCategory;
}

export enum EventCategory {
  CONCERT = 'CONCERT',
  CONFERENCE = 'CONFERENCE'
}
