import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {LocationSearchParam} from '../dtos/locationSearchParam';
import {SmallLocation} from '../dtos/smallLocation';
import {BigLocationSearchParams} from "../dtos/bigLocationSearchParams";
import {Location} from "../dtos/location";

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

  /**
   * Finds all locations with given parameters
   *
   * @param searchParams properties which the location should have.
   */
  findAllLocationsBy(searchParams: BigLocationSearchParams): Observable<Location[]>{
    console.log(`Find all Locations with search params: ${searchParams}`);
    let terms = new HttpParams();
    if(searchParams.name) {
      terms = terms.set('name', searchParams.name);
    }
    if(searchParams.city) {
      terms = terms.set('city', searchParams.city);
    }
    if(searchParams.country) {
      terms = terms.set('country', searchParams.country);
    }
    if(searchParams.street) {
      terms = terms.set('street', searchParams.street);
    }
    if(searchParams.zipCode) {
      terms = terms.set('zipCode', searchParams.zipCode);
    }
    return this.http.get<Location[]>(baseUri + '/search', {params: terms});
  }
}
