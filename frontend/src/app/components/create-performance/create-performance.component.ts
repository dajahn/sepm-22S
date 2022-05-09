import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NgbDate, NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {Performance} from '../../dtos/performance';

@Component({
  selector: 'app-create-performance',
  templateUrl: './create-performance.component.html',
  styleUrls: ['./create-performance.component.scss']
})
export class CreatePerformanceComponent implements OnInit {
  @Input()
  performance: Performance;
  @Input()
  number: number;
  @Output()
  deletePerformanceWithNumber = new EventEmitter<number>();
  @Output()
  updatePerformanceEmitter = new EventEmitter< { number: number; performance: Performance }>();

  time: any;
  dateModel: NgbDateStruct;
  location: string;

  constructor() {

  }

  ngOnInit(): void {
    this.time = {
      hour: this.performance.dateTime.getHours(),
      minute: this.performance.dateTime.getMinutes()
    };
    this.dateModel = {
      year: this.performance.dateTime.getFullYear(),
      month: this.performance.dateTime.getMonth()+1,
      day: this.performance.dateTime.getDate()
    };
  }

  delete() {
    this.deletePerformanceWithNumber.emit(this.number);
  }

  updatePerformance() {
    this.performance.dateTime = new Date(this.dateModel.year, this.dateModel.month-1, this.dateModel.day, this.time.hour, this.time.minute);
    this.performance.location = null;
    this.updatePerformanceEmitter.emit({
      number: this.number,
      performance: this.performance
    });
  }
}
