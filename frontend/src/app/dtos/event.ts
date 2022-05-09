import {Artist} from './artist';
import {EventCategory} from '../enums/event-category';

export interface Event {
  id?: number;
  name: string;
  description: string;
  duration: number;
  thumbnail: File;
  category: EventCategory;
  artists: Artist[];
  performances: Performance[];
}
