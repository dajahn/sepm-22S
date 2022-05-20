import {EventCategory} from './event';
import {DurationUtil} from '../utils/duration-util';

export class EventSearchParams {
  id?: number;
  name?: string;
  description?: string;
  category?: EventCategory;
  duration?: DurationUtil;
  artistId?: number;
}
