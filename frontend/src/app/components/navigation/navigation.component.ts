import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

  currentRoute: string;
  // toggle extended mobile menu to hide/show
  showMobileMenu = false;

  // TODO Add router links to different pages
  readonly routerLinks = {
    home: '/',
    logIn: '/login',
    signUp: '/register',
    editProfile: '/edit',
    search: '/search',
    news: '/news',
    topTen: '/events/top-ten-events',
    cart: '/cart',
    reservations: '/reservations',
    adminTools: '/admintools'
  };



  constructor(public authService: AuthService,
              private router: Router) {

    // Checks if the route is changing. If the route equals a navigation item, highlight it
    router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.currentRoute = event.url;
    });
  }

  ngOnInit(): void { }




  /**
   * Shows and hides the mobile extended navigation
   * Disables scroll on the menu is shown
   */
  openMobileMenu() {
    // disables scrollbar when mobile menu is expanded
    if (this.showMobileMenu) {
      document.body.style.overflow = 'auto';
    } else {
      document.body.style.overflow = 'hidden';
    }
    this.showMobileMenu = !this.showMobileMenu;
  }

  /**
   * Signs the current user out
   */
  signOut() {
    this.authService.logoutUser();
    this.showMobileMenu = false;
  }

  /**
   * Navigates to the home page
   */
  clickHome() {
    this.router.navigate([this.routerLinks.home]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the news page
   */
  clickNews() {
    this.router.navigate([this.routerLinks.news]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the top 10 page
   */
  clickTopTen() {
    this.router.navigate([this.routerLinks.topTen]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the search page
   */
  clickSearch() {
    this.router.navigate([this.routerLinks.search]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the cart page
   */
  clickCart() {
    this.router.navigate([this.routerLinks.cart]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the reservation page
   */
  clickReservations() {
    this.router.navigate([this.routerLinks.reservations]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the admin page
   */
  clickAdmin() {
    this.router.navigate([this.routerLinks.adminTools]).then(r => this.showMobileMenu = false);
  }
}
