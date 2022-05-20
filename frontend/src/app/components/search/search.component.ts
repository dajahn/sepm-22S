import { Component, OnInit } from '@angular/core';
import {Globals} from "../../global/globals";
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {PerformanceSearchParams} from "../../dtos/performanceSearchParams";
import {Performance} from "../../dtos/performance";
import {EventSearchParams} from "../../dtos/eventSearchParams";
import {ArtistSearchParams} from "../../dtos/artistSearchParams";
import {EventService} from "../../services/event.service";
import {PerformanceService} from "../../services/performance.service";
import {ArtistService} from "../../services/artist.service";
import {LocationService} from "../../services/location.service";
import {Artist} from "../../dtos/artist";
import {Event, EventCategory} from "../../dtos/event";
import {BigLocationSearchParams} from "../../dtos/bigLocationSearchParams";
import {Location} from "../../dtos/location";
import {CountriesCodeToName} from "../../enums/countriesCodeToName";
import {DurationUtil} from "../../utils/duration-util";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
  performanceSearchParams: PerformanceSearchParams;
  performances: Performance[];
  eventSearchParams: EventSearchParams;
  events: Event[];
  artistSearchParams: ArtistSearchParams;
  artists: Artist[];
  locationSearchParams: BigLocationSearchParams;
  locations: Location[];
  categories = EventCategory;
  categoriesValues = [];
  countries = CountriesCodeToName;
  countriesValues = [];

  searchedPerformances = false;
  searchedArtists = false;
  searchedEvents = false;
  searchedLocations = false;

  constructor(private artistService: ArtistService,
              private locationService: LocationService,
              private eventService: EventService,
              private performanceService: PerformanceService,
              private globals: Globals,
              private router: Router,
              public authService: AuthService) {}

  ngOnInit(): void {
    this.performanceSearchParams = new PerformanceSearchParams();
    this.eventSearchParams = new EventSearchParams();
    this.artistSearchParams = new ArtistSearchParams();
    this.locationSearchParams = new BigLocationSearchParams();
    this.categoriesValues = Object.values(this.categories);
    this.countriesValues = Object.values(this.countries);
    console.log(this.categoriesValues);
  }

  findPerformances(){
    console.log('find performances with terms: {}', this.performanceSearchParams);
    this.performanceService.findAllPerformancesBy(this.performanceSearchParams).subscribe(
      (data) => {
        this.performances = data;
        console.log(data);
      },
      error => {
        console.error('Error searching horse', error.message);
        alert('Could not find Performance with this properties');
      }
    );
  }

  findArtists(){
    console.log('find artists with name: {}', this.artistSearchParams.name);
    this.artistService.search(this.artistSearchParams).subscribe(
      (data) => {
        this.artists = data;
      },
      error => {
        console.error('Error searching artists', error.message);
        alert('Could not find Artists with this properties');
      }
    );
  }

  findEvents(){
    console.log('find events with params: {}', this.eventSearchParams);
    this.eventService.findAllEventsBy(this.eventSearchParams).subscribe(
      (data) => {
        this.events = data;
      },
      error => {
        console.error('Error searching artists', error.message);
        alert('Could not find Artists with this properties');
      }
    );
  }

  findLocations() {
    console.log('find locations with params: {}', this.locationSearchParams);
    this.locationService.findAllLocationsBy(this.locationSearchParams).subscribe(
      (data) => {
        this.locations = data;
      },
      error => {
        console.error('Error searching artists', error.message);
        alert('Could not find Locations with this properties');
      }
    );
  }
}
