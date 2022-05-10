import { NewsService } from './../../services/news.service';
import { Component, OnInit } from '@angular/core';
import { News } from 'src/app/dtos/news';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {
  public news: News[];

  constructor(private newsService: NewsService) { }

  ngOnInit(): void {
    this.loadNews();
  }

  private loadNews() {
    this.newsService.getAllNews().subscribe((news: News[]) => {
      this.news = news;
    });
  }

}
