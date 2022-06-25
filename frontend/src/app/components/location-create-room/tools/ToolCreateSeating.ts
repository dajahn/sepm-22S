import { SeatType } from '../../../dtos/seat-sector';
import { Point } from '../../../dtos/point';
import { SectorType } from '../../../dtos/sector';
import { LocationCreateRoomComponent } from '../location-create-room.component';
import { normalizePointSet, PointSet, Tool, ToolType } from './Tool';
import { Seat } from '../../../dtos/seat';

export default class ToolCreateSeating implements Tool {
  private readonly ref: LocationCreateRoomComponent;
  private readonly type: SeatType;

  constructor( ref: LocationCreateRoomComponent, type: SeatType ) {
    this.ref = ref;
    this.type = type;
  }

  startPreview( start: Point ): void {
    const area = normalizePointSet(start, start);

    this.ref.previewSector = {
      id: null,
      price: 0,
      name: '',
      type: SectorType.SEAT,
      seatType: this.type,
      preview: true,
      seats: getSeatsForArea( area ),
    };
    this.ref.updateLocation(true);
  }

  updatePreview( start: Point, current: Point ): void {
    const item = this.ref.previewSector;
    item.seats = getSeatsForArea( normalizePointSet(start, current) );
    this.ref.updateLocation(true);
  }

  createSelection(start: Point, end: Point) {
    this.ref.previewSector = null;
    const existingSector = this.ref.seatingSectors.find(item => item.seatType === this.type );

    const area: PointSet = normalizePointSet(start, end);

    const seats = getSeatsForArea( area );

    this.ref.deleteItemsInArea( area );

    if (existingSector) {
      existingSector.seats = [ ...existingSector.seats, ...seats ];
    } else {
      this.ref.seatingSectors.push({
        price: 0,
        name: '',
        type: SectorType.SEAT,
        seatType: this.type,
        seats,
        preview: false,
      });
    }

    this.ref.updateLocation();
  }

  getToolType(): ToolType {
    switch ( this.type ) {
      case SeatType.NONE:
        return ToolType.CREATE_SEATING_NONE;
      case SeatType.PREMIUM:
        return ToolType.CREATE_SEATING_PREMIUM;
      case SeatType.VIP:
        return ToolType.CREATE_SEATING_VIP;
      default:
        return null;
    }
  }

}

const getSeatsForArea = ( { point1, point2 }: PointSet): Seat[] => {
  const seats = [];

  for ( let x = point1.x; x < point2.x; x++ ) {
    for ( let y = point1.y; y < point2.y; y++ ) {
      seats.push({
        row: 0,
        column: 0,
        point: { x, y },
      });
    }
  }

  return seats;
};
