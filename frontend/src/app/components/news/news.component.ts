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
  public allNews: News[];

  public expandAllNews: boolean = false;
  public currentSelectedIdUnreadNews: number = -1;
  public currentSelectedIdAllNews: number = -1;


  constructor(private newsService: NewsService) { }

  ngOnInit(): void {
    this.loadNews();
  }

  //TODO: Only load UNREAD news
  private loadNews() {
    this.newsService.getAllNews().subscribe((news: News[]) => {
      this.news = news;

      for (let n of this.news) {
        n.fileDto.image = 'data:image/jpeg;base64,' + n.fileDto.image;
      }
      console.dir(this.news);
    });
  }

  private loadAllNews() {
    this.newsService.getAllNews().subscribe((news: News[]) => {
      this.allNews = news;

      for (let n of this.allNews) {
        n.fileDto.image = 'data:image/jpeg;base64,' + n.fileDto.image;
      }
    });
  }

  public handelOnClickUnreadNews(index: number) {
    this.currentSelectedIdUnreadNews = index;
    this.currentSelectedIdAllNews = -1;
  }

  public handelOnClickAllNews(index: number) {
    this.currentSelectedIdAllNews = index;
    this.currentSelectedIdUnreadNews = -1;
  }

  public toggleExpandAllNews() {
    this.expandAllNews = !this.expandAllNews;

    //Load all news only when needed
    this.loadAllNews();
  }

}
