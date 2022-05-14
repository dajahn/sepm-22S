import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {LocationSearchParam} from '../dtos/locationSearchParam';
import {SmallLocation} from '../dtos/smallLocation';

const baseUri = new Globals().backendUri + '/locations';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  constructor(private http: HttpClient) {
  }

  search(searchParams: LocationSearchParam): Observable<SmallLocation[]> {
    let p = new HttpParams();
    if (searchParams.name) {
      p = p.set('name', searchParams.name);
    }
    if (searchParams.maxRecords) {
      p = p.set('maxRecords', searchParams.maxRecords);
    }
    return this.http.get<SmallLocation[]>(baseUri, {params: p});
  }
}
