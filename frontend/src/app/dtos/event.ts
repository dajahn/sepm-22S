import {Artist} from './artist';
import {Performance} from './performance';
import {EventCategory} from '../enums/event-category';

export interface Event {
  id?: number;
  name: string;
  description: string;
  duration: string;
  thumbnail: File;
  category: EventCategory;
  artists: Artist[];
  performances: Performance[];
}
