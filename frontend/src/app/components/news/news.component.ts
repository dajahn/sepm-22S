import { AuthService } from './../../services/auth.service';
import { Router } from '@angular/router';
import { Globals } from './../../global/globals';
import { NewsService } from './../../services/news.service';
import { Component, OnChanges, OnInit, SimpleChange, SimpleChanges } from '@angular/core';
import { News, PagedNewsDto } from 'src/app/dtos/news';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent implements OnInit {
  //Pagination configuration, same for both
  public pageSize = 5;

  //Current page for unread / all news
  public page = 1;
  public allNewsPage = 1;

  //Unread News
  public news: News[] = [];
  public totalUnreadNews = 0;

  //All News
  public allNews: News[];
  public totalAllNews = 0;

  //Styling variables
  public expandAllNews = false;
  public currentSelectedIdUnreadNews = -1;
  public currentSelectedIdAllNews = -1;

  constructor(private newsService: NewsService, private globals: Globals, private router: Router, public authService: AuthService) {
  }

  ngOnInit(): void {
    this.loadUnreadNews();
  }

  //Page Change of UnreadNewsPagination
  public handlePageChange() {
    this.loadUnreadNews();
  }

  public handlePageChangeAllNews() {
    this.loadAllNews();
  }

  private loadUnreadNews() {
    this.newsService.getUnread(this.page - 1, this.pageSize).subscribe((pagedNews: PagedNewsDto) => {
      this.news = pagedNews.news;
      this.totalUnreadNews = pagedNews.totalCount;

      for (const n of this.news) {
        n.fileDto.url = this.globals.backendUri + n.fileDto.url;
      }
      console.dir(pagedNews);
    });
  }

  private loadAllNews() {
    this.newsService.getAllNews(this.allNewsPage - 1, this.pageSize).subscribe((pagedNews: PagedNewsDto) => {
      this.allNews = pagedNews.news;
      this.totalAllNews = pagedNews.totalCount;

      for (const n of this.allNews) {
        n.fileDto.url = this.globals.backendUri + n.fileDto.url;
      }
    });
  }

  //On a small screen new pages opens
  private redirectOnSmallScreens(id) {
    if (window.innerWidth <= 1300) {
      this.router.navigate(['/news', id]);
    }
  }

  public handelOnClickUnreadNews(index: number) {
    const id: number = this.news[index].id;
    this.redirectOnSmallScreens(id);
    this.readNewsById(id);

    this.currentSelectedIdUnreadNews = index;
    this.currentSelectedIdAllNews = -1;
  }

  public handelOnClickAllNews(index: number) {
    const id = this.allNews[index].id;
    this.redirectOnSmallScreens(id);
    this.readNewsById(id);
    this.currentSelectedIdAllNews = index;
    this.currentSelectedIdUnreadNews = -1;
  }


  //The News gets set as read on getById
  public readNewsById(id: number) {
    //The News gets set as read on getById
    this.newsService.getById(id).subscribe(n => {
      console.log(n);
    });

  }

  public toggleExpandAllNews() {
    this.expandAllNews = !this.expandAllNews;

    //Load all news only when needed
    this.loadAllNews();
  }

}
