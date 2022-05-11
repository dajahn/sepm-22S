import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {Performance} from '../../dtos/performance';
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
  updatePerformanceEmitter = new EventEmitter<{ number: number; performance: Performance }>();
  performance: Performance;
  time: any;
  dateModel: NgbDateStruct;
  location: SmallLocation;
  searchLocationFailed: boolean;

  constructor(private locationService: LocationService) {

  }

  ngOnInit(): void {
    this.performance = {
      id: null,
      location: null,
      dateTime: new Date(),
      event: null
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
  }

  updatePerformance() {
    this.performance.dateTime = new Date(this.dateModel.year,
      this.dateModel.month - 1, this.dateModel.day, this.time.hour, this.time.minute);
    this.performance.location = {
      id: this.location.id,
      performances: null,
      name: this.location.name,
      address: this.location.address,
      sectors: null
    };
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
            // this.showDanger('Something went wrong while fetching the possible fathers');
            return of([]);
          }));
      }),
      catchError(() => {
        this.searchLocationFailed = true;
        // this.showDanger('Something went wrong while fetching the possible mothers');
        return of([]);
      })
    );
}
