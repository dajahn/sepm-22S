import { environment } from './../../environments/environment';
import { News } from './../dtos/news';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private newsBaseURI = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  public createNews(news: News) {
    return this.httpClient.post(this.newsBaseURI, news);
  }

  public getAllNews() {
    return this.httpClient.get(this.newsBaseURI);
  }
}
