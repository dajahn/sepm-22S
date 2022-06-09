import { Point } from '../../../dtos/point';
import { StandingSector } from '../../../dtos/standing-sector';
import { LocationCreateRoomComponent } from '../location-create-room.component';
import { isPointWithinArea, Tool, ToolType } from './Tool';
import { SeatSector } from '../../../dtos/seat-sector';
import { SectorType } from '../../../dtos/sector';

export default class ToolSelect implements Tool {
  private readonly ref: LocationCreateRoomComponent;

  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {}

  updatePreview( start: Point, current: Point ): void {}

  createSelection(start: Point, end: Point): void {
    const sectors: (SeatSector | StandingSector)[] = [
      ...this.ref.standingSectors.filter(
        sector => isPointWithinArea(end, { point1: sector.point1, point2: sector.point2 })
      ),
      ...this.ref.seatingSectors.filter(
        sector => sector.seats.filter(seat => seat.point.x === end.x && seat.point.y === end.y).length > 0
      ),
    ];

    if (!sectors || sectors.length === 0) {
      return;
    }

    const target = sectors[0];

    if (target.type === SectorType.STANDING) {
      this.ref.openEditStandingSectorModal(target as StandingSector);
      return;
    }

    if (target.type === SectorType.SEAT) {
      this.ref.openEditSeatingSectorModal(target as SeatSector);
      return;
    }

  }

  getToolType(): ToolType {
    return ToolType.SELECT;
  }
}
