import {SectorType} from './sector';

export interface CreateTicket {
  performance: number;
  type: SectorType;
  item: number;
}
