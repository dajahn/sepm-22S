import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LocationCreateRoomComponent } from './location-create-room.component';

describe('LocationCreateRoomComponent', () => {
  let component: LocationCreateRoomComponent;
  let fixture: ComponentFixture<LocationCreateRoomComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LocationCreateRoomComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationCreateRoomComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
