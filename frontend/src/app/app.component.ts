import {Component} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  constructor(private router: Router) {
  }

  /**
   * Checks if the router url contains the specified route
   *
   * @param route
   * @returns
   */
  hasRoute(route: string) {
    return this.router.url.includes(route);
  }

}
