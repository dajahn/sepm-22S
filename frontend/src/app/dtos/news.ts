import { FileDto } from './file';

export class News {
    id?: number;
    title: string;
    description: string;
    eventId: number;
    date?: string;
    fileDto?: FileDto;
    image?: File;
}