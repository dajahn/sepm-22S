import { FileDto } from './file';

export class News {
    id: number;
    title: string;
    description: string;
    eventId: number;
    image: File;
    date: string;
    fileDto?: FileDto;
}