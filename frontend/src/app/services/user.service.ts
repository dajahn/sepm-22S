import { UserSearchDto } from './../dtos/user';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import { CreateUser, User } from '../dtos/user';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AuthRequest } from '../dtos/auth-request';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authBaseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  createUser(user: CreateUser): Observable<User> {
    return this.httpClient.post<User>(this.authBaseUri, user);
  }

  loadUser(userSearch: UserSearchDto): Observable<User[]> {
    let queryParams: string = Object.keys(userSearch).map(key => key + '=' + userSearch[key]).join('&');
    return this.httpClient.get<User[]>(this.authBaseUri + "?" + queryParams);
  }

  unlockUser(id: number): Observable<User> {
    return this.httpClient.get<User>(this.authBaseUri + "/unlock/" + id);
  }

  lockUser(id: number): Observable<User> {
    return this.httpClient.get<User>(this.authBaseUri + "/lock/ " + id);
  }
}
