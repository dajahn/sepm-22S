import {FileDto} from './file';
import {EventCategory} from './event';

export interface TopTenEvent {
  id: number;
  name: string;
  thumbnail: FileDto;
  category: EventCategory;
  ticketCount: number;
}
