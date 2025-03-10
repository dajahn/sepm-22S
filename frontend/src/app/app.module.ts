import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { httpInterceptorProviders } from './interceptors';
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
import { RegisterComponent } from './components/register/register.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { NewsCreateAddEventComponent } from './components/news-create-add-event/news-create-add-event.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { EditAccountComponent } from './components/edit-account/edit-account.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { CreateUserComponent } from './components/create-user/create-user.component';
import { SearchComponent } from './components/search/search.component';
import { TopTenEventsComponent } from './components/top-ten-events/top-ten-events.component';
import { LocationComponent } from './components/event/location/location.component';
import { EventComponent } from './components/event/event.component';
import { CapitalizeFirstPipe } from './pipes/capitalize-first.pipe';
import { PricePipe } from './pipes/price.pipe';
import { DurationPipe } from './pipes/duration.pipe';
import { LocationCreateRoomComponent } from './components/location-create-room/location-create-room.component';
import { LocationCreateComponent } from './components/location-create/location-create.component';
import { ReservationsComponent } from './components/reservations/reservations.component';
import {DatePipe} from '@angular/common';
import {ShortNumberPipe} from './pipes/short-number.pipe';

@NgModule({
  declarations: [
    AppComponent,
    NavigationComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    CartComponent,
    TicketComponent,
    NewsCreateComponent,
    NewsComponent,
    NewsDetailComponent,
    CreateEventComponent,
    CreatePerformanceComponent,
    CreateEventAddArtistComponent,
    ToastComponentComponent,
    RegisterComponent,
    EditAccountComponent,
    NewsCreateAddEventComponent,
    CheckoutComponent,
    NewsCreateAddEventComponent,
    UserManagementComponent,
    EditAccountComponent,
    ResetPasswordComponent,
    CreateUserComponent,
    SearchComponent,
    TopTenEventsComponent,
    LocationComponent,
    EventComponent,
    DurationPipe,
    CapitalizeFirstPipe,
    PricePipe,
    LocationCreateRoomComponent,
    LocationCreateComponent,
    ShortNumberPipe,
    ReservationsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
  ],
  providers: [httpInterceptorProviders, DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule {
}
