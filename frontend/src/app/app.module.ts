import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FooterComponent } from './components/footer/footer.component';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { MessageComponent } from './components/message/message.component';
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
import { CreateUserComponent } from './components/create-user/create-user.component';

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
    RegisterComponent,
    NewsCreateAddEventComponent,
    UserManagementComponent,
    EditAccountComponent,
    CreateUserComponent
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
