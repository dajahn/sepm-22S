import {Sector} from './sector';
import {Point} from './point';

export interface StandingSector extends Sector {
  capacity: number;
  point1: Point;
  point2: Point;
}
