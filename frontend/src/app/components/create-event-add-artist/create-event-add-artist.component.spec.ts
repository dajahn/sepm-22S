import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateEventAddArtistComponent } from './create-event-add-artist.component';

describe('CreateEventAddArtistComponent', () => {
  let component: CreateEventAddArtistComponent;
  let fixture: ComponentFixture<CreateEventAddArtistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateEventAddArtistComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateEventAddArtistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
