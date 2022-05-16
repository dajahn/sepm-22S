import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { NewsCreateComponent } from './components/news-create/news-create.component';
import { NewsComponent } from './components/news/news.component';
import { NewsDetailComponent } from './components/news-detail/news-detail.component';
import { CreateEventComponent } from './components/create-event/create-event.component';
import { CreatePerformanceComponent } from './components/create-performance/create-performance.component';
import { CreateEventAddArtistComponent } from './components/create-event-add-artist/create-event-add-artist.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { CartComponent } from './components/cart/cart.component';
import { TicketComponent } from './components/ticket/ticket.component';
import { ToastComponentComponent } from './components/toast-component/toast-component.component';
import {TopTenEventsComponent} from './components/top-ten-events/top-ten-events.component';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    CartComponent,
    TicketComponent,
    NewsCreateComponent,
    NewsComponent,
    NewsDetailComponent,
    CreateEventComponent,
    CreatePerformanceComponent,
    CreateEventAddArtistComponent,
    ToastComponentComponent,
    TopTenEventsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
