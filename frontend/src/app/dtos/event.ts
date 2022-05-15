import {Artist} from './artist';
import {FileDto} from './file';
import {CreatePerformance} from './performance';

export interface Event {
  id: number;
  name: string;
  description: string;
  duration: string; // must be string cause of parsing. Duration datatype in backend is a string when parsed into/from JSON
  artists: Artist[];
  category: EventCategory;
  thumbnail: FileDto;
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
  thumbnail: FileDto;
  artists: Artist[];
  category: EventCategory;
  performances: CreatePerformance[];
}
