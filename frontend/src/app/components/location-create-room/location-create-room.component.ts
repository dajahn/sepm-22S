import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Location } from '../../dtos/location';
import { SectorType } from '../../dtos/sector';
import { SeatSector, SeatType } from '../../dtos/seat-sector';
import { Point } from '../../dtos/point';
import { addPoints, Seat } from '../../dtos/seat';
import { StandingSector } from '../../dtos/standing-sector';

@Component({
  selector: 'app-location-create-room',
  templateUrl: './location-create-room.component.html',
  styleUrls: ['./location-create-room.component.scss']
})
export class LocationCreateRoomComponent implements OnInit, AfterViewInit {

  @ViewChild('locationRef') locationRef: ElementRef<HTMLInputElement>;

  size: Point = {x: 25, y: 10};
  standingSectors: StandingSector[] = [];
  seatingSectors: SeatSector[] = [];
  previewSector = null;
  tool: Tool = new ToolCreateStanding(this);

  startPos: Point = null;
  currentPos: Point = null;

  location: Location = null;

  history: History = { undo: [], redo: [] };

  // Enum Mapping
  ToolType = ToolType;
  Direction = Direction;

  constructor() {
    this.updateLocation(true);
  }

  ngOnInit(): void {}

  updateLocation(soft = false): void {
    this.updateStandingSectorNames();

    const location = {
      id: 1,
      name: 'test',
      address: null,
      sectors: [
        ...this.standingSectors,
        ...this.seatingSectors,
      ]
    };

    if (!soft) {
      this.history.undo.push(JSON.parse(JSON.stringify({
        standingSectors: this.standingSectors,
        seatingSectors: this.seatingSectors,
      })));

      this.history.redo = [];
    }

    if (this.previewSector) {
      location.sectors.push(this.previewSector);
    }
    this.location = location;
  }

  updateStandingSectorNames() {
    const sectors = this.standingSectors.sort((a, b) => 5 * (a.point1.x - b.point1.x) + (a.point1.y - b.point1.y));
    const needNumber = sectors.length > 26;
    sectors.forEach((item, i) => {
      if ( needNumber ) {
        item.name = '#' + (i + 1);
      } else {
        item.name = String.fromCharCode(i % 26 + 65);
      }
    });
  }

  parsePagePosition( point: Point ): Point {
    const elem = this.locationRef.nativeElement;

    const res = {
      x: Math.floor( ( point.x - elem.offsetLeft ) / ( elem.clientWidth / ( this.size.x + 1 ) ) ),
      y: Math.floor( ( point.y - elem.offsetTop ) / ( elem.clientHeight / ( this.size.y + 1 ) ) ),
    };

    if (res.x > this.size.x) {
      res.x = this.size.x;
    }
    if (res.y > this.size.y) {
      res.y = this.size.y;
    }
    return res;
  }

  selectTool(type: ToolType) {
    switch ( type ) {
      case ToolType.CREATE_STANDING:
        this.tool = new ToolCreateStanding(this);
        break;
      case ToolType.CREATE_SEATING_NONE:
        this.tool = new ToolCreateSeating(this, SeatType.NONE);
        break;
      case ToolType.CREATE_SEATING_PREMIUM:
        this.tool = new ToolCreateSeating(this, SeatType.PREMIUM);
        break;
      case ToolType.CREATE_SEATING_VIP:
        this.tool = new ToolCreateSeating(this, SeatType.VIP);
        break;
      case ToolType.DELETE:
        this.tool = new ToolDelete(this);
        break;
      case ToolType.SELECT:
        this.tool = new ToolSelect(this);
        break;
    }
  }

  undo() {
    if (this.history.undo.length === 0) {
      return;
    }

    const state: State = this.history.undo.pop();
    this.history.redo.push(state);

    this.loadLastState();
  }

  redo() {
    if (this.history.redo.length === 0) {
      return;
    }

    const state: State = this.history.redo.pop();
    this.history.undo.push(state);

    this.loadLastState();
  }

  loadLastState() {
    const current = this.history.undo[ this.history.undo.length - 1 ];

    this.standingSectors = JSON.parse(JSON.stringify(current?.standingSectors || []));
    this.seatingSectors = JSON.parse(JSON.stringify(current?.seatingSectors || []));

    this.updateLocation(true);
  }

  expand(direction: Direction) {
    switch ( direction ) {
      case Direction.FRONT:
        this.moveAllItems({ x: 0, y: 1 });
        this.size.y++;
        break;
      case Direction.RIGHT:
        this.size.x++;
        break;
      case Direction.BACK:
        this.size.y++;
        break;
      case Direction.LEFT:
        this.moveAllItems({ x: 1, y: 0 });
        this.size.x++;
        break;
    }
  }

  moveAllItems( directionVector: Point ) {
    for (const sector of this.standingSectors) {
      sector.point1 = addPoints(sector.point1, directionVector);
      sector.point2 = addPoints(sector.point2, directionVector);
    }

    for (const sector of this.seatingSectors) {
      for (const seat of sector.seats) {
        seat.point = addPoints(seat.point, directionVector);
      }
    }
  }

  deleteItemsAtPoint( target: Point, soft = true) {
    this.standingSectors = this.standingSectors.filter(
      sector => !isPointWithinArea(target, { point1: sector.point1, point2: sector.point2 })
    );

    for (const sector of this.seatingSectors) {
      sector.seats = sector.seats.filter( seat => !(seat.point.x === target.x && seat.point.y === target.y));
    }

    this.updateLocation(soft);
  }

  deleteItemsInArea( area: PointSet ) {
    for ( let x = area.point1.x; x < area.point2.x; x++ ) {
      for ( let y = area.point1.y; y < area.point2.y; y++ ) {
        this.deleteItemsAtPoint({ x, y });
      }
    }
  }

  ngAfterViewInit(): void {
    const elem = this.locationRef.nativeElement;

    elem.addEventListener('mousedown', (e) => {
      if (e.buttons !== 1) {
        return;
      }
      e.preventDefault();
      this.startPos = this.parsePagePosition({ x: e.pageX, y: e.pageY });
      this.tool.startPreview(this.startPos);
    });

    elem.addEventListener('mousemove', (e) => {
      if (!this.startPos) {
        return;
      }
      e.preventDefault();
      const position = this.parsePagePosition({ x: e.pageX, y: e.pageY});

      if (this.currentPos === position) {
        return;
      }

      this.tool.updatePreview(this.startPos, position);
    });

    elem.addEventListener('mouseup', (e) => {
      e.preventDefault();
      const position = this.parsePagePosition({ x: e.pageX, y: e.pageY });

      this.tool.createSelection(this.startPos, position);
      this.startPos = null;
    });
  }
}

enum ToolType {
  CREATE_STANDING,
  CREATE_SEATING_NONE,
  CREATE_SEATING_PREMIUM,
  CREATE_SEATING_VIP,
  DELETE,
  SELECT,
}

enum Direction {
  FRONT,
  RIGHT,
  BACK,
  LEFT,
}

declare interface Tool {
  startPreview(current: Point ): void;
  updatePreview(start: Point, current: Point ): void;
  createSelection(start: Point, end: Point ): void;
  getToolType(): ToolType;
}

class ToolCreateStanding implements Tool {
  private ref: LocationCreateRoomComponent;


  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {
    this.ref.previewSector = {
      id: null,
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
      id: null,
      price: 0,
      name: 'abcd',
      capacity: 0,
      type: SectorType.STANDING,
      ...area,
      preview: false,
    });
    this.ref.updateLocation();
  }

  getToolType(): ToolType {
    return ToolType.CREATE_STANDING;
  }

}

class ToolCreateSeating implements Tool {
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
        id: 1,
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

class ToolDelete implements Tool {
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

class ToolSelect implements Tool {
  private readonly ref: LocationCreateRoomComponent;

  constructor( ref: LocationCreateRoomComponent ) {
    this.ref = ref;
  }

  startPreview( start: Point ): void {}

  updatePreview( start: Point, current: Point ): void {}

  createSelection(start: Point, end: Point) {}

  getToolType(): ToolType {
    return ToolType.SELECT;
  }
}

const getSeatsForArea = ( { point1, point2 }: PointSet): Seat[] => {
  const seats = [];

  for ( let x = point1.x; x < point2.x; x++ ) {
    for ( let y = point1.y; y < point2.y; y++ ) {
      seats.push({
        id: 0,
        row: 0,
        column: 0,
        point: { x, y },
      });
    }
  }

  return seats;
};

const isPointWithinArea = ( target: Point, area: PointSet ): boolean => (
  area.point1.x <= target.x && target.x < area.point2.x && area.point1.y <= target.y && target.y < area.point2.y
);

const normalizePointSet = (p1: Point, p2: Point): PointSet => {
  const point1: Point = { x: p1.x <= p2.x ? p1.x : p2.x, y: p1.y <= p2.y ? p1.y : p2.y};
  const point2: Point = { x: p1.x > p2.x ? p1.x : p2.x, y: p1.y > p2.y ? p1.y : p2.y};
  point2.x += 1;
  point2.y += 1;
  return { point1, point2 };
};

interface PointSet {
  point1: Point;
  point2: Point;
}

interface History {
  undo: State[];
  redo: State[];
}

interface State {
  standingSectors: StandingSector[];
  seatingSectors: SeatSector[];
}
