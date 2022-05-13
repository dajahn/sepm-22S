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
    //TODO: change to json request encode file base64
    /*
      const formData = new FormData();
      formData.append('image', news.image, news.image.name);
      formData.append('title', news.title);
      formData.append('eventId', news.eventId.toString());
      formData.append('description', news.description);
    */


    return this.httpClient.post(this.newsBaseURI, news);
  }

  public getAllNews() {
    return this.httpClient.get(this.newsBaseURI);
  }
}
