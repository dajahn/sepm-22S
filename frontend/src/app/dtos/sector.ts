export interface Sector {
  id: number;
  type: SectorType;
  price: number;
}

export enum SectorType {
  STANDING_SECTOR = 'STANDING_SECTOR',
  SEAT_SECTOR = 'SEAT_SECTOR',
}
