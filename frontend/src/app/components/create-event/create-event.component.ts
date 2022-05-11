import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventCategory} from '../../enums/event-category';
import {Performance} from '../../dtos/performance';
import {Artist} from '../../dtos/artist';
import {Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {DurationUtil} from '../../utils/duration-util';

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
  image: File;

  eventFormMessages = {
    title: [
      {type: 'required', message: 'Title is required'},
      {type: 'minLength', message: 'Title must contain at least 4 characters'}
    ],
    duration: [
      {type: 'required', message: 'Duration is required'},
      {type: 'minValue', message: 'Duration must be grater than 0 minutes'}
    ],
    description: [
      {type: 'required', message: 'Description is required'}
    ],
    category: [
      {type: 'required', message: 'Category is required'}
    ],
    thumbnail: [
      {type: 'required', message: 'Thumbnail is required'}
    ],
    artists: [
      {type: 'required', message: 'At minimum 1 artist is required'}
    ],
    performances: [
      {type: 'required', message: 'At minimum 1 performance is required'}
    ]

  };
  artistButtonClicked = false;
  performanceButtonClicked = false;
  imageButtonClicked = false;

  constructor(private formBuilder: FormBuilder, private eventService: EventService) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.compose([Validators.required, Validators.minLength(4)])]],
      duration: ['', [Validators.required]],
      category: ['', [Validators.required]],
      description: ['', [Validators.required]],
      thumbnail: ['', [Validators.required]]
    });
    this.performances = [];
    this.artists = [];
  }

  ngOnInit(): void {
  }

  addEvent() {
    const event: Event = {
      id: null,
      name: this.eventForm.controls.title.value,
      artists : this.artists,
      thumbnail : this.image,
      description: this.eventForm.controls.description.value,
      performances: this.performances,
      duration: DurationUtil.stringRepresentation(this.eventForm.controls.duration.value),
      category: this.eventForm.controls.category.value,
    };

    console.log(event);
    this.eventService.save(event).subscribe({
      next: value => {
        console.log(value);
      },
      error: err => {
        console.error(err);
      }
    });

  }

  addPerformance() {
    this.performanceButtonClicked = true;
    this.performances.push({
      dateTime: new Date(),
      event: null,
      id: null,
      location: null,
    });
  }

  updatePerformance($event: { number: number; performance: Performance }) {
    this.performances[$event.number] = $event.performance;
    console.log(this.performances);
  }

  deletePerformance($event: number) {
    this.performances.splice($event, 1);
  }

  addArtist() {
    this.artistButtonClicked = true;
    this.artists.push({
      name: '',
      id: null,
      description: null
    });
  }

  deleteArtist($event: number) {
    this.artists.splice($event, 1);
  }

  updateArtist($event: { number: number; artist: Artist }) {
    this.artists[$event.number] = $event.artist;
    // console.log('par--');
    // console.log($event);
    console.log(this.artists);
  }

  public handleFileInput(files: any) {
    this.imageButtonClicked = true;
    this.image = files[0];
  }

}
