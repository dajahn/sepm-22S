import { Globals } from './../../global/globals';
import { NewsService } from './../../services/news.service';
import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { News } from 'src/app/dtos/news';

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit {
  @Input() public news: News;

  constructor(private route: ActivatedRoute, private newsService: NewsService, private globals: Globals) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.hasOwnProperty('id'))
        this.loadNewsDetail(params['id']);
    })
  }

  private loadNewsDetail(id: number) {
    this.newsService.getById(id).subscribe((newsDto: News) => {
      this.news = newsDto;
      this.news.fileDto.url = this.globals.backendUri + this.news.fileDto.url;
    });
  }



}
