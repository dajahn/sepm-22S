import { ToastService } from './../../services/toast-service.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NewsService } from './../../services/news.service';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { News } from 'src/app/dtos/news';
import { Router } from '@angular/router';
import { Event } from 'src/app/dtos/event';
import { FileDto } from 'src/app/dtos/file';

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
  public fileDto: FileDto;
  public previewImage: string;

  public submitted = false;
  public addEvent = false;

  constructor(private newsService: NewsService, private formBuilder: FormBuilder
    , private toastService: ToastService, private router: Router) {
    this.newsForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.maxLength(255)]],
      imageDescription: ['', [Validators.required, Validators.maxLength(255)]],
      image: ['', [Validators.required]],
      eventId: ['']
    });

    this.fileDto = {};
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

    if (!this.newsForm.valid) {
      return;
    }

    const news: News = {
      title: this.newsForm.controls.title.value,
      description: this.newsForm.controls.description.value,
      imageDescription: this.newsForm.controls.imageDescription.value,
      fileDto: {
        ...this.fileDto
      }
    };

    if (this.event != null) {
      news.eventDto = {
        ...this.event
      };
    }

    this.newsService.createNews(news).subscribe({
      next: value => {
        this.showSuccess(`News created with title ${news.title}!`);
        this.router.navigate(['/news']);
      },
      error: err => {
        this.showDanger('Something went wrong!');
      }
    });
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, { classname: 'bg-danger text-light', delay: 5000 });
  }

  public toggleAddEvent() {
    this.addEvent = !this.addEvent;
  }

  public async handleFileInput(files: any) {
    if (files == null) {
      this.image = null;
      this.fileDto = {};
      return;
    }

    this.image = files[0];

    //Sets the base64 of the dto
    let base64img = await this.getBase64(this.image);
    this.previewImage = base64img;
    base64img = base64img.split(',')[1];
    this.fileDto.imageBase64 = base64img;
    this.fileDto.type = this.image.type;
  }

  public removeImageCandidate() {
    this.newsForm.controls.image.setValue(null);
    this.handleFileInput(null);
  }
}
