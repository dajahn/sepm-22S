import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';
import {CreateUser, User} from '../dtos/user';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }

  createUser(user: CreateUser): Observable<User> {
    return this.httpClient.post<User>(this.authBaseUri, user);
  }

  forgotPassword(email: string): Observable<void> {
    return this.httpClient.post<void>(this.authBaseUri + '/forgot-password', { email });
  }
}
