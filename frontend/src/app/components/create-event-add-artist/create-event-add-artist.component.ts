import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ArtistSearchParams} from '../../dtos/artistSearchParams';
import {catchError, debounceTime, distinctUntilChanged, Observable, of, OperatorFunction, switchMap} from 'rxjs';
import {tap} from 'rxjs/operators';
import {ArtistService} from '../../services/artist.service';
import {Artist} from '../../dtos/artist';

@Component({
  selector: 'app-create-event-add-artist',
  templateUrl: './create-event-add-artist.component.html',
  styleUrls: ['./create-event-add-artist.component.scss']
})
export class CreateEventAddArtistComponent implements OnInit {
  @Input()
  number: number;
  @Output()
  deleteArtistWithNumber = new EventEmitter<number>();
  @Output()
  updateArtistEmitter = new EventEmitter<{ number: number; artist: Artist }>();

  searchArtistFailed = false;
  artistError = false;
  artist: Artist;
  deleted = false;

  constructor(private artistService: ArtistService) {
  }

  ngOnInit(): void {
  }

  delete() {
    this.deleted = true;
    this.deleteArtistWithNumber.emit(this.number);
  }

  updateArtist() {
    const tmp = this.artist;
    this.updateArtistEmitter.emit({
      number: this.number,
      artist: tmp
    });
    if (typeof this.artist !== 'object' || this.artist === undefined || this.artist === null) {
      this.artistError = true;
    } else {
      this.artistError = false;
    }
  }


  resultFormatArtistListValue(value: Artist) {
    return value.name;
  }

  inputFormatArtistListValue(value: Artist) {
    if (value.name) {
      return value.name;
    }
    return value;
  }


  searchArtist: OperatorFunction<string, readonly string[]> = (text$: Observable<string>) =>
    text$.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(name => {
        const params = new ArtistSearchParams();
        params.name = name;
        params.maxRecords = 5;
        return this.artistService.search(params).pipe(
          tap(() => this.searchArtistFailed = false),
          catchError(() => {
            this.searchArtistFailed = true;
            // this.showDanger('Something went wrong while fetching the possible fathers');
            return of([]);
          }));
      }),
      catchError(() => {
        this.searchArtistFailed = true;
        // this.showDanger('Something went wrong while fetching the possible mothers');
        return of([]);
      })
    );

}
