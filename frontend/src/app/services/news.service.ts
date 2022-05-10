import { News } from './../dtos/news';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NewsService {
  private baseURI: string = "http://localhost:8080/api/v1/news";

  constructor(private httpClient: HttpClient) { }

  public createNews(news: News) {
    const formData = new FormData();
    formData.append('image', news.image, news.image.name);
    formData.append('title', news.title);
    formData.append('eventId', news.eventId.toString());
    formData.append('description', news.description);

    return this.httpClient.post(this.baseURI, formData);
  }

  public getAllNews() {
    return this.httpClient.get(this.baseURI);
  }
}
