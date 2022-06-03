export interface Sector {
  id: number;
  type: SectorType;
  price: number;
  name: string;
  preview: boolean;
}

export enum SectorType {
  STANDING = 'STANDING',
  SEAT = 'SEAT',
}
