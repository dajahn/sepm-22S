import { News, PagedNewsDto } from './../dtos/news';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private newsBaseURI = this.globals.backendUri + '/news';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  public createNews(news: News) {
    return this.httpClient.post(this.newsBaseURI, news);
  }

  public getById(id: number) {
    return this.httpClient.get(this.newsBaseURI + '/' + id);
  }

  public getAllNews(page: number = 0, size: number = 5, loadUnread: boolean = false): Observable<PagedNewsDto> {
    return this.httpClient.get<PagedNewsDto>(this.newsBaseURI + '?page=' + page + '&size=' + size + '&loadUnread=' + loadUnread);
  }
}
