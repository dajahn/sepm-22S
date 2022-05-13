import {Artist} from './artist';
import {FileDto} from './file';

export interface Event {
  id: number;
  name: string;
  description: string;
  duration: number;
  artists: Artist[];
  category: EventCategory;
  thumbnail: FileDto;
}

export enum EventCategory {
  CONCERT = 'CONCERT',
  CONFERENCE = 'CONFERENCE'
}
