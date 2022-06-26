import { PagedUserDto, UserSearchDto } from './../dtos/user';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { AuthService } from './auth.service';
import { CreateUpdateUser, User } from '../dtos/user';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { AuthRequest } from '../dtos/auth-request';

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

  loadUser(userSearch: UserSearchDto, page: number, pageSize: number): Observable<PagedUserDto> {
    let queryParams: string = Object.keys(userSearch).map(key => key + '=' + userSearch[key]).join('&');
    queryParams += `&page=${page}&size=${pageSize}`;
    return this.httpClient.get<PagedUserDto>(this.baseUri + '?' + queryParams);
  }

  updateLockingState(id: number, locked: boolean): Observable<User> {
    return this.httpClient.put<User>(this.baseUri + '/lockingState/' + id, locked);
  }

  deleteUser(): Observable<void> {
    return this.httpClient.delete<void>(this.baseUri);
  }
}
