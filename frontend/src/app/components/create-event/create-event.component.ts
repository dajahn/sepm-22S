import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CreatePerformance} from '../../dtos/performance';
import {Artist} from '../../dtos/artist';
import {CreateEvent, EventCategory} from '../../dtos/event';
import {EventService} from '../../services/event.service';
import {DurationUtil} from '../../utils/duration-util';
import {ToastService} from '../../services/toast-service.service';
import {Router} from '@angular/router';
import {FileDto} from '../../dtos/file';
import {ErrorMessageParser} from '../../utils/error-message-parser';

@Component({
  selector: 'app-create-event',
  templateUrl: './create-event.component.html',
  styleUrls: ['./create-event.component.scss']
})
export class CreateEventComponent implements OnInit {
  eventForm: FormGroup;
  eventCategories = EventCategory;
  eventCategoriesKeys = [];
  time: any;
  performances: CreatePerformance[];
  artists: Artist[];
  image: FileDto;
  backGroundImg: File;
  public previewImage: string;

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

  constructor(private formBuilder: FormBuilder, private eventService: EventService,
              private toastService: ToastService, private router: Router) {
    this.eventForm = this.formBuilder.group({
      title: ['', [Validators.compose([Validators.required, Validators.minLength(4)])]],
      duration: ['', [Validators.required]],
      category: [EventCategory.CONCERT, [Validators.required]],
      description: ['', [Validators.required]],
      thumbnail: ['', [Validators.required]]
    });
    this.performances = [];
    this.artists = [];
    this.eventCategoriesKeys = Object.keys(this.eventCategories);
    console.log(this.eventCategoriesKeys);
  }

  ngOnInit(): void {
  }

  async addEvent() {
    const createPerformances = this.filterPerformances();
    for (const item of createPerformances) {
      const hoursDiff = item.dateTime.getHours() - item.dateTime.getTimezoneOffset() / 60;
      item.dateTime.setHours(hoursDiff);
    }
    console.log(createPerformances);

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

    this.eventService.save(event).subscribe({
      next: value => {
        this.showSuccess(`Event created with tile '${event.name}'! YAY`);
        this.router.navigate([`/events/${value.id}`]);
      },
      error: err => {
        this.showDanger(ErrorMessageParser.parseResponseToErrorMessage(err));
      }
    });
  }

  public getBase64(file: File) {
    return new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
      reader.onload = () => resolve(<string>reader.result);
      reader.onerror = (error) => reject(error);
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

  public async handleFileInput(files: any) {
    if (files === null) {
      this.backGroundImg = null;
      this.imageButtonClicked = true;
      this.image = null;
      this.previewImage = null;
    } else {
      this.backGroundImg = files[0];
      this.imageButtonClicked = true;
      let base64img = await this.getBase64(files[0]);
      this.previewImage = base64img;
      base64img = base64img.split(',')[1];
      this.image = {
        imageBase64: base64img,
        type: files[0].type
      };
    }
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

  removeImageCandidate() {
    this.handleFileInput(null);
  }

  validPerformances() {
    let b = true;
    const tmp = new Date();
    this.performances.forEach(perf => {
      if (perf !== null && (perf.location === null || perf.location === undefined)) {
        b = false;
      } else if (perf != null && (perf.location.id === undefined || perf.location.name === undefined)) {
        b = false;
      } else if (perf != null && perf.dateTime.getHours() === 0 && perf.dateTime.getMinutes() === 0) {
        b = false;
      } else if (perf !== null && (isNaN(perf.dateTime.valueOf()) || perf.dateTime < tmp)) {
        b = false;
      }
    });
    return b;
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger text-light', delay: 5000});
  }
}
