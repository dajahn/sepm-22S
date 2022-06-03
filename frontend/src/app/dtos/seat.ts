import {Point} from './point';

export interface Seat {
  id: number;
  row: number;
  column: number;
  point: Point;
}

export const addPoints = (point1: Point, point2: Point): Point => ({ x: point1.x + point2.x, y: point1.y + point2.y });
