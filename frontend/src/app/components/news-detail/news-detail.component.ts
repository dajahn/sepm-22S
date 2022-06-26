import { Globals } from './../../global/globals';
import { NewsService } from './../../services/news.service';
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { News } from 'src/app/dtos/news';

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent implements OnInit, OnChanges {
  @Input() public news: News;

  public link: string;

  constructor(private route: ActivatedRoute, private newsService: NewsService, private globals: Globals) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.setLink();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.setLink();
      if (params.hasOwnProperty('id')) {
        this.loadNewsDetail(params['id']);
      }
    });
  }

  private loadNewsDetail(id: number) {
    this.newsService.getById(id).subscribe((newsDto: News) => {
      this.news = newsDto;
      this.news.fileDto.url = this.globals.backendUri + this.news.fileDto.url;
      if (this.news.eventDto) {
        this.news.eventDto.thumbnail.url = this.globals.backendUri + this.news.eventDto.thumbnail.url;
      }
      this.setLink();
    });
  }

  private setLink() {
    //When eventId exists set linkt to event
    if (this.news && this.news.eventDto && this.news.eventDto.id) {
      this.link = '/events/' + this.news.eventDto.id;
    }
  }
}
