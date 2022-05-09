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

    this.newsService.createNews(this.news).subscribe((resp) => {
      debugger;
      console.log(resp);
    });
  }

  loadNews() {
    this.newsService.getNews().subscribe((news) => {
      console.dir(news);
    });
  }

  public handleFileInput(files: any) {
    //TODO: Possibility to upload multiple files?
    this.news.image = files[0];
  }
}
