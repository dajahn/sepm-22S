import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { NewsService } from './../../services/news.service';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { News } from 'src/app/dtos/news';

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {
  public newsForm: FormGroup;
  public image: File;
  public submitted: boolean = false;

  constructor(private newsService: NewsService, private formBuilder: FormBuilder) {
    this.newsForm = this.formBuilder.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      image: [''],
      eventId: ['']
    });
  }

  ngOnInit(): void {
  }

  public getBase64(file: File) {
    return new Promise<string>((resolve, reject) => {
      let reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => resolve(<string>reader.result);
      reader.onerror = (error) => reject(error);
    })
  }

  async onSubmit(e: Event) {
    e.preventDefault();

    this.submitted = true;

    let base64img = await this.getBase64(this.image);
    base64img = base64img.split(',')[1];

    let news: News = {
      title: this.newsForm.controls.title.value,
      description: this.newsForm.controls.description.value,
      eventId: this.newsForm.controls.eventId.value,
      fileDto: {
        imageBase64: base64img,
        type: this.image.type
      }
    };

    //TODO: Add validation toast
    if (news.title.trim() == "" || news.title.length > 255) {
      console.error("News title not accepted!");
      return;
    } else if (news.description.trim() == "" || news.description.length > 255) {
      console.error("News description not accepted!");
      return;
    }

    //TOD: Add toast response
    this.newsService.createNews(news).subscribe((resp) => {
      console.log(resp);
    });
  }

  public handleFileInput(files: any) {
    //TODO: Possibility to upload multiple files?
    this.image = files[0];
  }
}
