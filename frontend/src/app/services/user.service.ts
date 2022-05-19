import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../global/globals';
import {CreateUpdateUser, User} from '../dtos/user';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private baseUri: string = this.globals.backendUri + '/users';

  constructor(private httpClient: HttpClient, private globals: Globals) {
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

  forgotPassword(email: string): Observable<void> {
    return this.httpClient.post<void>(this.baseUri + '/forgot-password', { email });
  }

  resetPassword(hash: string, password: string): Observable<void> {
    return this.httpClient.post<void>(this.baseUri + '/reset-password', { hash, password });
  }
}
