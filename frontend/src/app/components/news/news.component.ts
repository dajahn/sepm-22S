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

  public currentSelectedId: number = -1;

  constructor(private newsService: NewsService) { }

  ngOnInit(): void {
    this.loadNews();
  }

  private loadNews() {
    this.newsService.getAllNews().subscribe((news: News[]) => {
      this.news = news;
      console.log(this.news);
      for (let n of this.news) {
        n.fileDto.image = 'data:image/jpeg;base64,' + n.fileDto.image;
      }
      console.dir(this.news);
    });
  }

  public handelOnClick(index: number) {
    this.currentSelectedId = index;
  }

}
