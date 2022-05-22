export interface Sector {
  id: number;
  type: SectorType;
  price: number;
  name: string;
}

export enum SectorType {
  STANDING = 'STANDING',
  SEAT = 'SEAT',
}
