import {CreateEventComponent} from './components/create-event/create-event.component';
import {AdminGuard} from './guards/admin.guard';
import {NewsComponent} from './components/news/news.component';
import {NewsCreateComponent} from './components/news-create/news-create.component';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {NewsDetailComponent} from './components/news-detail/news-detail.component';
import {CartComponent} from './components/cart/cart.component';
import {RegisterComponent} from './components/register/register.component';
import {EditAccountComponent} from './components/edit-account/edit-account.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'account/edit', canActivate: [AuthGuard], component: EditAccountComponent},
  {path: 'event/create', canActivate: [AdminGuard], component: CreateEventComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'news', canActivate: [AuthGuard], component: NewsComponent},
  {path: 'news/create', canActivate: [AdminGuard], component: NewsCreateComponent},
  {path: 'news/:id', component: NewsDetailComponent},
  {path: 'cart', component: CartComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
