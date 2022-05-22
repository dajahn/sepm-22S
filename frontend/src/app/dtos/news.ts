import {FileDto} from './file';
import {Event} from './event';

export class News {
  id?: number;
  title: string;
  description: string;
  imageDescription: string;
  date?: string;
  fileDto?: FileDto;
  eventDto?: Event;
}
