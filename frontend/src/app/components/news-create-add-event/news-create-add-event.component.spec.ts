import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsCreateAddEventComponent } from './news-create-add-event.component';

describe('NewsCreateAddEventComponent', () => {
  let component: NewsCreateAddEventComponent;
  let fixture: ComponentFixture<NewsCreateAddEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewsCreateAddEventComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewsCreateAddEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
