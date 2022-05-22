import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Globals} from '../global/globals';
import {ArtistSearchParams} from '../dtos/artistSearchParams';
import {Observable} from 'rxjs';
import {Artist} from '../dtos/artist';

const baseUri = new Globals().backendUri + '/artists';

@Injectable({
  providedIn: 'root'
})
export class ArtistService {

  constructor(private http: HttpClient) {
  }

  search(searchParams: ArtistSearchParams): Observable<Artist[]> {
    let p = new HttpParams();
    if (searchParams.name) {
      p = p.set('name', searchParams.name);
    }
    if (searchParams.maxRecords) {
      p = p.set('maxRecords', searchParams.maxRecords);
    }
    return this.http.get<Artist[]>(baseUri, {params: p});
  }
}
