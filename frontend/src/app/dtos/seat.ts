import {Point} from './point';

export interface Seat {
  id?: number;
  row: number;
  column: number;
  point: Point;
}
