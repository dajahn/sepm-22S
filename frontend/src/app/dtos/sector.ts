export interface Sector {
  id: number;
  type: SectorType;
  price: number;
}

export enum SectorType {
  STANDING = 'STANDING',
  SEAT = 'SEAT',
}
