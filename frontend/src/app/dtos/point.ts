export interface Point {
  x: number;
  y: number;
}

export const addPoints = (point1: Point, point2: Point): Point => ({ x: point1.x + point2.x, y: point1.y + point2.y });
export const findMin = (point1: Point, point2: Point): Point => ({ x: Math.min(point1.x, point2.x), y: Math.min(point1.y, point2.y) });
