import { environment } from './../../environments/environment';
import { News } from './../dtos/news';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  constructor(private httpClient: HttpClient) { }

  public createNews(news: News) {
    const formData = new FormData();
    formData.append('image', news.image, news.image.name);
    formData.append('title', news.title);
    formData.append('eventId', news.eventId.toString());
    formData.append('description', news.description);

    return this.httpClient.post(environment.baseURI, formData);
  }

  public getNews() {
    return this.httpClient.get(environment.baseURI);
  }
}
