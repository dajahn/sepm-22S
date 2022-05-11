import {Address} from './address';
import {Sector} from './sector';
import {StandingSector} from './standing-sector';
import {SeatSector} from './seat-sector';

export interface Location {
  id: number;
  name: string;
  address: Address;
  sectors: (StandingSector | SeatSector)[];
}
