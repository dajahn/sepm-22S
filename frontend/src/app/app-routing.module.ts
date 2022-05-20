import { CreateEventComponent } from './components/create-event/create-event.component';
import { AdminGuard } from './guards/admin.guard';
import { NewsComponent } from './components/news/news.component';
import { NewsCreateComponent } from './components/news-create/news-create.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { AuthGuard } from './guards/auth.guard';
import { MessageComponent } from './components/message/message.component';
import { NewsDetailComponent } from './components/news-detail/news-detail.component';
import { CartComponent } from './components/cart/cart.component';
import { RegisterComponent } from './components/register/register.component';
import { EditAccountComponent } from './components/edit-account/edit-account.component';
import { UserManagementComponent } from './components/user-management/user-management.component';
import { CreateUserComponent } from './components/create-user/create-user.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'events/create', canActivate: [AdminGuard], component: CreateEventComponent },
  { path: 'message', canActivate: [AuthGuard], component: MessageComponent },
  { path: 'news', canActivate: [AuthGuard], component: NewsComponent },
  { path: 'news/create', canActivate: [AdminGuard], component: NewsCreateComponent },
  { path: 'news/:id', component: NewsDetailComponent },
  { path: 'cart', component: CartComponent },
  { path: 'account/edit', canActivate: [AuthGuard], component: EditAccountComponent },
  { path: 'users', canActivate: [AdminGuard], component: UserManagementComponent, pathMatch: 'full' },
  { path: 'users/admins/create', canActivate: [AdminGuard], component: CreateUserComponent },
  { path: '', component: HomeComponent },
  { path: 'reset-password/:hash', component: ResetPasswordComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
