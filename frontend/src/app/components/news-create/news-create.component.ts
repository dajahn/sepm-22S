import { NewsService } from './../../services/news.service';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { News } from 'src/app/dtos/news';

@Component({
  selector: 'app-news-create',
  templateUrl: './news-create.component.html',
  styleUrls: ['./news-create.component.scss']
})
export class NewsCreateComponent implements OnInit {
  public news: News = {
    id: 0,
    title: '',
    description: '',
    eventId: 0,
    image: null,
    date: null
  };

  constructor(private newsService: NewsService) { }

  ngOnInit(): void {
  }

  onSubmit(e: Event) {
    e.preventDefault();

    //TODO: Add validation to toast
    if (this.news.title.trim() == "" || this.news.title.length > 255) {
      console.error("News title not accepted!");
      return;
    } else if (this.news.description.trim() == "" || this.news.description.length > 255) {
      console.error("News description not accepted!");
      return;
    }

    this.newsService.createNews(this.news).subscribe((resp) => {
      console.log(resp);
    });
  }

  public handleFileInput(files: any) {
    //TODO: Possibility to upload multiple files?
    this.news.image = files[0];
  }
}
