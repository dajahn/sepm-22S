import { AfterViewInit, Component, ElementRef, EventEmitter, OnInit, Output, Input, ViewChild } from '@angular/core';
import { Location } from '../../dtos/location';
import { SectorType } from '../../dtos/sector';
import { SeatSector, SeatType } from '../../dtos/seat-sector';
import { addPoints, findMin, Point } from '../../dtos/point';
import { Seat } from '../../dtos/seat';
import { StandingSector } from '../../dtos/standing-sector';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Tool, History, ToolType, State, isPointWithinArea, PointSet, Direction } from './tools/Tool';
import ToolCreateStanding from './tools/ToolCreateStanding';
import ToolCreateSeating from './tools/ToolCreateSeating';
import ToolDelete from './tools/ToolDelete';
import ToolSelect from './tools/ToolSelect';
import { ToastService } from '../../services/toast-service.service';

@Component({
  selector: 'app-location-create-room',
  templateUrl: './location-create-room.component.html',
  styleUrls: ['./location-create-room.component.scss']
})
export class LocationCreateRoomComponent implements OnInit, AfterViewInit {

  @Input()  data: Location = null;
  @Output() dataChange = new EventEmitter<Location>();

  @Output() export = new EventEmitter<void>();

  @ViewChild('locationRef') locationRef: ElementRef<HTMLInputElement>;
  @ViewChild('editStandingSectorModal') editStandingSectorModalRef: ElementRef<HTMLInputElement>;
  @ViewChild('editSeatingSectorModal') editSeatingSectorModalRef: ElementRef<HTMLInputElement>;
  public editStandingSectorForm: FormGroup;
  public editSeatingSectorForm: FormGroup;

  size: Point = {x: 25, y: 10};
  standingSectors: StandingSector[] = [];
  seatingSectors: SeatSector[] = [];
  previewSector = null;
  tool: Tool = new ToolCreateStanding(this);

  editStandingSector: StandingSector = null;
  editSeatingSector: SeatSector = null;

  startPos: Point = null;
  currentPos: Point = null;

  location: Location = null;

  history: History = { undo: [], redo: [] };

  // Enum Mapping
  ToolType = ToolType;
  Direction = Direction;

  constructor(
    private modalService: NgbModal,
    private formBuilder: FormBuilder,
    private toastService: ToastService,
  ) {
    this.editStandingSectorForm = this.formBuilder.group({
      capacity: [0, []],
      price: [0, []],
    });

    this.editSeatingSectorForm = this.formBuilder.group({
      price: [0, []],
    });

    this.updateLocation(true);
  }

  ngOnInit(): void {
    this.location = this.data;
    this.seatingSectors = this.data.sectors.filter(item => item.type === SectorType.SEAT).map(item => item as SeatSector);
    this.standingSectors = this.data.sectors.filter(item => item.type === SectorType.STANDING).map(item => item as StandingSector);
  }

  exportLocation() {
    const location = JSON.parse(JSON.stringify(this.location));

    const invalidSectors = [
      ...this.seatingSectors.filter(item => !item.price),
      ...this.standingSectors.filter(item => !item.price || !item.capacity)
    ];

    if (invalidSectors.length > 0) {
      this.toastService.show(
        'All Sectors must have a price and Standing Sectors must also specify a capacity. ' +
        'Make sure to set the values using the "Select Tool" before saving!',
        {classname: 'bg-danger', delay: 5000}
      );
      return;
    }

    /* Crop whitespace */

    // find minimum
    const minimum = location.sectors.reduce( (min: Point, sector: (StandingSector | SeatSector)) => {
      if (sector.type === SectorType.STANDING) {
        return findMin(min, (sector as StandingSector).point1);
      } else if (sector.type === SectorType.SEAT) {
        return (sector as SeatSector).seats.reduce((min1: Point, seat: Seat) => findMin(min1, seat.point), min);
      }
    }, this.size);

    const direction = { x: - minimum.x, y: - minimum.y };

    // move items
    location.sectors.forEach(item => {
      if (item.type === SectorType.STANDING) {
        const sector = item as StandingSector;
        sector.point1 = addPoints(sector.point1, direction);
        sector.point2 = addPoints(sector.point2, direction);
      } else if (item.type === SectorType.SEAT) {
        (item as SeatSector).seats.forEach( seat => {
          seat.point = addPoints(seat.point, direction);
        });
      }
    });

    /* add columns / rows to seats */
    const seatSectors = location.sectors.filter(item => item.type === SectorType.SEAT);
    // const firstSeat = seatSectors.reduce( (min: Point, sector: SeatSector) => (
    //     ( sector as SeatSector ).seats.reduce( ( min1: Point, seat: Seat ) => findMin( min1, seat.point ), min )
    //   ), this.size);

    let grid: Seat[][] = Array(this.size.x + 1).fill(0).map(() => Array(this.size.y + 1).fill(null));

    seatSectors.forEach((sector: SeatSector) => {
      sector.seats.forEach( (seat: Seat) => {
        if (!grid[seat.point.x]) {
          return;
        }
        grid[seat.point.x][seat.point.y] = seat;
      });
    });

    // remove empty columns and rows

    grid = grid?.filter(column => column.join('') !== '');

    if (grid) {
      for ( let y = 0; y < grid[0]?.length; y++ ) {
        let missingSeats = 0;
        for ( let x = 0; x < grid.length; x++ ) {
          const seat = grid[x][y];
          if (!seat) {
            missingSeats++;
            continue;
          }
          seat.row = y + 1;
          seat.column = x + 1 - missingSeats;
        }
      }
    }

    this.export.emit();
    this.dataChange.emit(location);
  }

  updateLocation(soft = false): void {
    this.updateStandingSectorNames();

    const location = {
      id: this.location?.id,
      name: this.location?.name,
      address: this.location?.address,
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
    const sectors = this.standingSectors.sort((a, b) => (a.point1.x - b.point1.x) + 5 * (a.point1.y - b.point1.y));
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

  clear() {
    this.standingSectors = [];
    this.seatingSectors = [];

    this.updateLocation();
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

  openEditStandingSectorModal(sector: StandingSector): void {
    this.editStandingSector = sector;
    this.editStandingSectorForm.reset();

    this.editStandingSectorForm.controls.capacity.setValue(this.editStandingSector.capacity || 0);
    this.editStandingSectorForm.controls.price.setValue(this.editStandingSector.price || 0);

    this.modalService.open(this.editStandingSectorModalRef, {ariaLabelledBy: 'modal-edit-standing-sector'}).result.then(() => {
      this.editStandingSector.capacity = this.editStandingSectorForm.controls.capacity.value || 0;
      this.editStandingSector.price = this.editStandingSectorForm.controls.price.value || 0;

      this.updateLocation();
    });
  }

  openEditSeatingSectorModal(sector: SeatSector): void {
    this.editSeatingSector = sector;
    this.editSeatingSectorForm.reset();

    this.editSeatingSectorForm.controls.price.setValue(this.editSeatingSector.price || 0);

    this.modalService.open(this.editSeatingSectorModalRef, {ariaLabelledBy: 'modal-edit-seating-sector'}).result.then(() => {
      this.editSeatingSector.price = this.editSeatingSectorForm.controls.price.value || 0;

      this.updateLocation();
    });
  }
}
