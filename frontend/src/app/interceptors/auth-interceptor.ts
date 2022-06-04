import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {AuthService} from '../services/auth.service';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  excluded_uris = [];

  constructor(private authService: AuthService, private globals: Globals) {

    this.excluded_uris = [
      { url: this.globals.backendUri + '/authentication'},
      { url: this.globals.backendUri + '/users', method: 'POST'},
      { url: this.globals.backendUri + '/users/forgot-password', method: 'POST'},
      { url: this.globals.backendUri + '/users/reset-password', method: 'POST'},
    ];
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authUri = this.globals.backendUri + '/authentication';
    const registerUri = this.globals.backendUri + '/users';
    // Do not intercept authentication requests or register requests

    const excludedItem = this.excluded_uris.find(item => item.url === req.url);
    const isRouteExcluded = excludedItem
      && (excludedItem.method ? excludedItem.method === req.method : true)
      && (this.authService.isLoggedIn() === false);

    if (isRouteExcluded) {
      return next.handle(req);
    }
    if (!this.authService.isLoggedIn()) {
      return next.handle(req);
    }
    const authReq = req.clone({
      headers: req.headers.set('Authorization', 'Bearer ' + this.authService.getToken())
    });

    return next.handle(authReq);
  }
}
