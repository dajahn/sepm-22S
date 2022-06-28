import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {CreatePerformance} from '../../dtos/performance';
import {LocationService} from '../../services/location.service';
import {catchError, debounceTime, distinctUntilChanged, Observable, of, OperatorFunction, switchMap} from 'rxjs';
import {tap} from 'rxjs/operators';
import {LocationSearchParam} from '../../dtos/locationSearchParam';
import {SmallLocation} from '../../dtos/smallLocation';

@Component({
  selector: 'app-create-performance',
  templateUrl: './create-performance.component.html',
  styleUrls: ['./create-performance.component.scss']
})
export class CreatePerformanceComponent implements OnInit {

  @Input()
  number: number;
  @Output()
  deletePerformanceWithNumber = new EventEmitter<number>();
  @Output()
  updatePerformanceEmitter = new EventEmitter<{ number: number; performance: CreatePerformance }>();
  performance: CreatePerformance;
  time: any;
  dateModel: NgbDateStruct;
  location: SmallLocation;
  searchLocationFailed: boolean;
  deleted = false;
  today: NgbDateStruct;
  locationError: boolean;
  dateInvalid: boolean;
  dateStructureInvalid: boolean;

  constructor(private locationService: LocationService) {

  }

  ngOnInit(): void {
    const date = new Date();
    this.today = {
      year: date.getFullYear(),
      month: date.getMonth() + 1,
      day: date.getDate()
    };
    this.performance = {
      eventId: null,
      location: null,
      dateTime: new Date(),
    };
    this.time = {
      hour: this.performance.dateTime.getHours(),
      minute: this.performance.dateTime.getMinutes()
    };
    this.dateModel = {
      year: this.performance.dateTime.getFullYear(),
      month: this.performance.dateTime.getMonth() + 1,
      day: this.performance.dateTime.getDate()
    };
  }

  delete() {
    this.deletePerformanceWithNumber.emit(this.number);
    this.deleted = true;
  }

  updatePerformance() {
    //check if date model is a valid date
    if (this.dateModel !== null && this.dateModel !== undefined) {
      const timestamp = new Date(this.dateModel.year, this.dateModel.month - 1, this.dateModel.day).valueOf();
      console.log(timestamp);
      this.dateStructureInvalid = isNaN(timestamp) === true;
    }
    //check if date + time is after now
    if (this.time !== null && this.time !== undefined && this.dateModel !== null && this.dateModel !== undefined) {
      const dt = new Date(this.dateModel.year, this.dateModel.month - 1, this.dateModel.day, this.time.hour, this.time.minute);
      this.dateInvalid = dt < new Date();
    }
    //safe date & time
    if (this.time !== null && this.dateModel !== null && this.dateModel !== undefined) {
      this.performance.dateTime = new Date(this.dateModel.year,
        this.dateModel.month - 1, this.dateModel.day, this.time.hour, this.time.minute);
    } else {
      this.performance.dateTime = new Date(null,
        null, null, null, null);
    }
    this.locationError = typeof this.location !== 'object' || this.location === undefined || this.location === null;
    if (this.location !== undefined) {
      this.performance.location = {
        id: this.location.id,
        name: this.location.name,
      };
    }
    this.updatePerformanceEmitter.emit({
      number: this.number,
      performance: this.performance
    });
  }

  resultFormatArtistListValue(value: SmallLocation) {
    return value.name;
  }

  inputFormatArtistListValue(value: SmallLocation) {
    if (value.name) {
      return value.name;
    }
    return value;
  }

  searchLocation: OperatorFunction<string, readonly string[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(name => {
        const params = new LocationSearchParam();
        params.name = name;
        params.maxRecords = 5;
        return this.locationService.search(params).pipe(
          tap(() => this.searchLocationFailed = false),
          catchError(() => {
            this.searchLocationFailed = true;
            return of([]);
          }));
      }),
      catchError(() => {
        this.searchLocationFailed = true;
        return of([]);
      })
    );
}
