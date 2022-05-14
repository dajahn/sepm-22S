import { NewsComponent } from './components/news/news.component';
import { NewsCreateComponent } from './components/news-create/news-create.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { MessageComponent } from './components/message/message.component';
import { NewsDetailComponent } from './components/news-detail/news-detail.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'message', canActivate: [AuthGuard], component: MessageComponent },
  { path: 'news',/*canActivate:[AuthGuard]*/ component: NewsComponent },
  { path: 'news/create', /*canActivate: [AuthGuard],*/ component: NewsCreateComponent },
  { path: 'news/:id', component: NewsDetailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
