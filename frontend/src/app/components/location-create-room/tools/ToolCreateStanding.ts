import { addPoints, Point, POINT_MINUS_ONE } from '../../../dtos/point';
import { SectorType } from '../../../dtos/sector';
import { LocationCreateRoomComponent } from '../location-create-room.component';
import { normalizePointSet, PointSet, Tool, ToolType } from './Tool';

export default class ToolCreateStanding implements Tool {
  private ref: LocationCreateRoomComponent;


  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {
    this.ref.previewSector = {
      price: 0,
      name: '',
      capacity: 0,
      type: SectorType.STANDING,
      ...normalizePointSet(start, start),
      preview: true,
    };
    this.ref.updateLocation(true);
  }

  updatePreview( start: Point, current: Point ): void {
    const item = this.ref.previewSector;
    const points = normalizePointSet(start, current);
    item.point1 = points.point1;
    item.point2 = points.point2;
    this.ref.updateLocation(true);
  }

  createSelection(start: Point, end: Point) {
    this.ref.previewSector = null;

    const area: PointSet = normalizePointSet(start, end);

    this.ref.deleteItemsInArea( area );

    this.ref.standingSectors.push({
      price: 0,
      name: '',
      capacity: 0,
      type: SectorType.STANDING,
      point1: area.point1,
      point2: addPoints(area.point2, POINT_MINUS_ONE),
      preview: false,
    });
    this.ref.updateLocation();
  }

  getToolType(): ToolType {
    return ToolType.CREATE_STANDING;
  }

}
