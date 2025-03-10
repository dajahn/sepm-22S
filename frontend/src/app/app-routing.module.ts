import {CreateEventComponent} from './components/create-event/create-event.component';
import {AdminGuard} from './guards/admin.guard';
import {NewsComponent} from './components/news/news.component';
import {NewsCreateComponent} from './components/news-create/news-create.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {ResetPasswordComponent} from './components/reset-password/reset-password.component';
import {AuthGuard} from './guards/auth.guard';
import {NewsDetailComponent} from './components/news-detail/news-detail.component';
import {CartComponent} from './components/cart/cart.component';
import {RegisterComponent} from './components/register/register.component';
import {TopTenEventsComponent} from './components/top-ten-events/top-ten-events.component';
import {EditAccountComponent} from './components/edit-account/edit-account.component';
import {UserManagementComponent} from './components/user-management/user-management.component';
import {CreateUserComponent} from './components/create-user/create-user.component';
import {EventComponent} from './components/event/event.component';
import {SearchComponent} from './components/search/search.component';
import {CheckoutComponent} from './components/checkout/checkout.component';
import {AntiAuthGuard} from './guards/anti-auth.guard';
import { LocationCreateComponent } from './components/location-create/location-create.component';
import {ReservationsComponent} from './components/reservations/reservations.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', canActivate: [AntiAuthGuard], component: LoginComponent},
  {path: 'register', canActivate: [AntiAuthGuard], component: RegisterComponent},
  {path: 'reset-password/:hash', canActivate: [AntiAuthGuard], component: ResetPasswordComponent},
  { path: 'locations/create', canActivate: [AdminGuard], component: LocationCreateComponent },
  {
    path: 'events', canActivate: [AuthGuard], children: [
      {path: 'topten', component: TopTenEventsComponent},
      {path: 'create', canActivate: [AdminGuard], component: CreateEventComponent},
      {
        path: ':eventId',
        canActivate: [AuthGuard],
        children: [
          {path: '', component: EventComponent},
          {path: 'performances/:performanceId', component: EventComponent}
        ]
      },
    ]
  },
  {path: 'message', redirectTo: '/'},
  {
    path: 'news', canActivate: [AuthGuard], children: [
      {path: '', component: NewsComponent},
      {path: 'create', canActivate: [AdminGuard], component: NewsCreateComponent},
      {path: ':id', component: NewsDetailComponent}
    ]
  },
  {path: 'account/edit', canActivate: [AuthGuard], component: EditAccountComponent},
  {
    path: 'users', canActivate: [AdminGuard], children: [
      {path: '', component: UserManagementComponent, pathMatch: 'full'},
      {path: 'admins/create', component: CreateUserComponent}
    ]
  },
  {path: 'search', canActivate: [AuthGuard], component: SearchComponent},
  {path: 'cart', canActivate: [AuthGuard], component: CartComponent},
  {path: 'checkout', canActivate: [AuthGuard], component: CheckoutComponent},
  {path: 'reservations', canActivate: [AuthGuard], component: ReservationsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    useHash: true,
    scrollPositionRestoration: 'enabled'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
