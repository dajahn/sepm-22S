import { Point } from '../../../dtos/point';
import { StandingSector } from '../../../dtos/standing-sector';
import { LocationCreateRoomComponent } from '../location-create-room.component';
import { isPointWithinArea, Tool, ToolType } from './Tool';

export default class ToolSelect implements Tool {
  private readonly ref: LocationCreateRoomComponent;

  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {}

  updatePreview( start: Point, current: Point ): void {}

  createSelection(start: Point, end: Point): void {
    const sectors: StandingSector[] = this.ref.standingSectors.filter(
      sector => isPointWithinArea(end, { point1: sector.point1, point2: sector.point2 })
    );
    if (sectors.length === 0) {
      return;
    }

    this.ref.openEditStandingSectorModal(sectors[0]);
  }

  getToolType(): ToolType {
    return ToolType.SELECT;
  }
}
