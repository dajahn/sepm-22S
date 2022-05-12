import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {EventCategory} from '../../enums/event-category';
import {CreatePerformance, Performance} from '../../dtos/performance';
import {Artist} from '../../dtos/artist';
import {CreateEvent, Event} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {DurationUtil} from '../../utils/duration-util';
import {ToastService} from '../../services/toast-service.service';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  eventForm: FormGroup;
  eventCategory = EventCategory;
  time: any;
  performances: CreatePerformance[];
  artists: Artist[];
  image: File; //TODO change to file dto or whatever

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

  constructor(private formBuilder: FormBuilder, private eventService: EventService, private toastService: ToastService) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.compose([Validators.required, Validators.minLength(4)])]],
      duration: ['', [Validators.required]],
      category: [this.eventCategory.CONCERT, [Validators.required]],
      description: ['', [Validators.required]],
      thumbnail: ['', [Validators.required]]
    });
    this.performances = [];
    this.artists = [];
  }

  ngOnInit(): void {
  }

  addEvent() {
    const createPerformances = this.filterPerformances();
    for (const item of createPerformances) {
      const hoursDiff = item.dateTime.getHours() - item.dateTime.getTimezoneOffset() / 60;
      item.dateTime.setHours(hoursDiff);
    }
    const event: CreateEvent = {
      id: null,
      name: this.eventForm.controls.title.value,
      artists: this.filterArtists(),
      thumbnail: this.image,
      description: this.eventForm.controls.description.value,
      duration: DurationUtil.stringRepresentation(this.eventForm.controls.duration.value),
      category: this.eventForm.controls.category.value,
      performances: createPerformances
    };

    console.log(event);
    this.eventService.save(event).subscribe({
      next: value => {
        this.showSuccess('Event created! YAY');
        console.log(value);
      },
      error: err => {
        this.showDanger('An error occurred: \n' + err.error.message);
        console.error(err);
      }
    });

  }

  addPerformance() {
    this.performanceButtonClicked = true;
    this.performances.push({
      dateTime: new Date(),
      location: null,
      eventId: null
    });
  }

  updatePerformance($event: { number: number; performance: CreatePerformance }) {
    this.performances[$event.number] = $event.performance;
  }

  deletePerformance($event: number) {
    this.performances[$event] = null;
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
    this.artists[$event] = null;
  }

  updateArtist($event: { number: number; artist: Artist }) {
    this.artists[$event.number] = $event.artist;
  }

  public handleFileInput(files: any) {
    this.imageButtonClicked = true;
    this.image = files[0];
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger text-light', delay: 5000});
  }

  filterArtists() {
    return this.artists.filter(art => art !== null);
  }

  filterPerformances() {
    return this.performances.filter(perf => perf !== null);
  }

  validArtists() {
    let b = true;
    this.artists.forEach(art => {
      if ((typeof art !== 'object' || art === undefined) && art !== null) {
        b = false;
      }
    });
    return b;
  }

  validPerformances() {
    let b = true;
    this.performances.forEach(perf => {
      if (perf  !== null && (perf.location=== null || perf.location === undefined)) {
        b = false;
      } else if (perf != null && (perf.location.id === undefined || perf.location.name === undefined)){
        b = false;
      } else if(perf != null && perf.dateTime.getHours() === 0 && perf.dateTime.getMinutes() === 0) {
        b = false;
      }
    });
    return b;
  }
}
