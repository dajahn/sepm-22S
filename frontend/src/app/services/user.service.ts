import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';
import {CreateUpdateUser, User} from '../dtos/user';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {AuthRequest} from '../dtos/auth-request';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  createUser(user: CreateUpdateUser): Observable<User> {
    return this.httpClient.post<User>(this.baseUri, user);
  }

  getOwnData(): Observable<User> {
    const uri = `${this.baseUri}/own`;
    return this.httpClient.get<User>(`${uri}`);
  }

  updateUser(updateUser: CreateUpdateUser, userId) {
    const uri = `${this.baseUri}/${userId}`;
    return this.httpClient.put<void>(uri, updateUser);
  }
}
