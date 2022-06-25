import { Point } from '../../../dtos/point';
import { LocationCreateRoomComponent } from '../location-create-room.component';
import { Tool, ToolType } from './Tool';

export default class ToolDelete implements Tool {
  private readonly ref: LocationCreateRoomComponent;

  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {
    this.ref.deleteItemsAtPoint(start);
  }

  updatePreview( start: Point, current: Point ): void {
    this.ref.deleteItemsAtPoint(current);
  }

  createSelection(start: Point, end: Point) {
    this.ref.deleteItemsAtPoint(end, false);
  }

  getToolType(): ToolType {
    return ToolType.DELETE;
  }
}
