import { AuthService } from './../../services/auth.service';
import { Router } from '@angular/router';
import { Globals } from './../../global/globals';
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


  constructor(private newsService: NewsService, private globals: Globals, private router: Router, public authService: AuthService) { }

  ngOnInit(): void {
    this.loadUnreadNews();
  }

  //TODO: Only load UNREAD news
  private loadUnreadNews() {
    this.newsService.getUnread().subscribe((news: News[]) => {
      this.news = news;

      for (let n of this.news) {
        n.fileDto.url = this.globals.backendUri + n.fileDto.url;
      }
    });
  }

  private loadAllNews() {
    this.newsService.getAllNews().subscribe((news: News[]) => {
      this.allNews = news;

      for (let n of this.allNews) {
        n.fileDto.url = this.globals.backendUri + n.fileDto.url;
      }
    });
  }

  private redirectOnSmallScreens(id) {
    if (window.innerWidth <= 1300) {
      this.router.navigate(['/news', id]);
    }
  }

  public handelOnClickUnreadNews(index: number) {
    let id: number = this.news[index].id;
    this.redirectOnSmallScreens(id);

    //The News gets set as read on getById
    this.newsService.getById(id).subscribe(n => {
      console.log(n);
    });

    this.currentSelectedIdUnreadNews = index;
    this.currentSelectedIdAllNews = -1;
  }

  public handelOnClickAllNews(index: number) {
    let id = this.allNews[index].id;
    this.redirectOnSmallScreens(id);
    this.currentSelectedIdAllNews = index;
    this.currentSelectedIdUnreadNews = -1;
  }

  public toggleExpandAllNews() {
    this.expandAllNews = !this.expandAllNews;

    //Load all news only when needed
    this.loadAllNews();
  }

}
