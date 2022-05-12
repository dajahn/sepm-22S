import {Artist} from './artist';
import {CreatePerformance} from './performance';

export interface Event {
  id: number;
  name: string;
  description: string;
  duration: string; // must be string cause of parsing. Duration datatype in backend is a string when parsed into/from JSON
  thumbnail: File;
  artists: Artist[];
  category: EventCategory;
}

export enum EventCategory {
  CONCERT = 'CONCERT',
  CONFERENCE = 'CONFERENCE'
}

export interface CreateEvent {
  id: number;
  name: string;
  description: string;
  duration: string; // must be string cause of parsing. Duration datatype in backend is a string when parsed into/from JSON
  thumbnail: File;
  artists: Artist[];
  category: EventCategory;
  performances: CreatePerformance[];
}
