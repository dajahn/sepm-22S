import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventCategory} from '../../enums/event-category';
import {Performance} from '../../dtos/performance';
import {Artist} from '../../dtos/artist';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  eventForm: FormGroup;
  eventCategory = EventCategory;
  time: any;
  performances: Performance[];
  artists: Artist[];

  constructor(private formBuilder: FormBuilder) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(4)]],
      duration: ['', [Validators.required]],
      category: ['', [Validators.required]],
      description: ['', [Validators.required]],
      thumbnail: ['', [Validators.required]]
    });
    this.performances = [];
    this.artists= [];
  }

  ngOnInit(): void {
  }

  addEvent() {
    //TODO
  }

  addPerformance() {
    this.performances.push({
      dateTime: new Date(),
      event: null,
      id: null,
      location: null,
    });
  }

  updatePerformance($event: { number: number; performance: Performance }) {
    this.performances[$event.number] = $event.performance;
  }

  deletePerformance($event: number) {
    this.performances.splice($event, 1);
  }

  addArtist() {
    this.artists.push({
      name: null,
      id: null,
      description: null
    });
  }

  deleteArtist($event: number) {
    this.artists.splice($event, 1);
  }

  updateArtist($event: { number: number; artist: Artist }) {
    this.artists[$event.number] = $event.artist;
  }
}
