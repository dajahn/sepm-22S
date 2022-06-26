import { Point } from '../../../dtos/point';
import { StandingSector } from '../../../dtos/standing-sector';
import { SeatSector } from '../../../dtos/seat-sector';

export declare interface Tool {
  startPreview(current: Point ): void;
  updatePreview(start: Point, current: Point ): void;
  createSelection(start: Point, end: Point ): void;
  getToolType(): ToolType;
}

export enum ToolType {
  CREATE_STANDING,
  CREATE_SEATING_NONE,
  CREATE_SEATING_PREMIUM,
  CREATE_SEATING_VIP,
  DELETE,
  SELECT,
}

export enum Direction {
  FRONT,
  RIGHT,
  BACK,
  LEFT,
}

export interface PointSet {
  point1: Point;
  point2: Point;
}

export interface History {
  undo: State[];
  redo: State[];
}

export interface State {
  standingSectors: StandingSector[];
  seatingSectors: SeatSector[];
}

export const isPointWithinArea = ( target: Point, area: PointSet ): boolean => (
  area.point1.x <= target.x && target.x <= area.point2.x && area.point1.y <= target.y && target.y <= area.point2.y
);

export const normalizePointSet = (p1: Point, p2: Point): PointSet => {
  const point1: Point = { x: p1.x <= p2.x ? p1.x : p2.x, y: p1.y <= p2.y ? p1.y : p2.y};
  const point2: Point = { x: p1.x > p2.x ? p1.x : p2.x, y: p1.y > p2.y ? p1.y : p2.y};
  point2.x += 1;
  point2.y += 1;
  return { point1, point2 };
};

