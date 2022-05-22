import {ToastService} from './../../services/toast-service.service';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {NewsService} from './../../services/news.service';
import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {News} from 'src/app/dtos/news';
import {Router} from '@angular/router';
import {Event} from 'src/app/dtos/event';

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {
  @ViewChild('imgUpload') imgUpload;

  public newsForm: FormGroup;
  public image: File;
  public event: Event;

  public submitted = false;
  public addEvent = false;

  constructor(private newsService: NewsService, private formBuilder: FormBuilder
    , private toastService: ToastService, private router: Router) {
    this.newsForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.maxLength(255)]],
      imageDescription: ['', [Validators.required, Validators.maxLength(255)]],
      image: [''],
      eventId: ['']
    });
  }

  ngOnInit(): void {
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

  public handleEventChange(event: Event) {
    this.event = event;
  }

  async onSubmit(e: any) {
    e.preventDefault();

    this.submitted = true;

    let base64img = await this.getBase64(this.image);
    base64img = base64img.split(',')[1];

    const news: News = {
      title: this.newsForm.controls.title.value,
      description: this.newsForm.controls.description.value,
      imageDescription: this.newsForm.controls.imageDescription.value,
      fileDto: {
        imageBase64: base64img,
        type: this.image.type
      },
      eventDto: {
        ...this.event
      }
    };


    this.newsService.createNews(news).subscribe({
      next: value => {
        this.showSuccess(`News created with title ${news.title}!`);
        this.router.navigate(['/news']);
      },
      error: err => {
        this.showDanger('An error occurred: \n' + err.error.message);
      }
    });
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger text-light', delay: 5000});
  }

  public toggleAddEvent() {
    this.addEvent = !this.addEvent;
  }

  public handleFileInput(files: any) {
    //TODO: Possibility to upload multiple files?
    if (files == null) {
this.image = null;
}

    this.image = files[0];
  }

  public removeImageCandidate() {
    this.imgUpload.nativeElement.value = null;
    this.handleFileInput(null);
  }
}
