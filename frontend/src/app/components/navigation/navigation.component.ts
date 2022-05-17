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
  // toggle admin menu to hide/show
  showAdminMenu = false;

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
    createNews: '/news/create',
    createEvent: '/events/create',
    createLocation: '/locations/create',
    addAdmin: '/admins/add',
    unlockUser: '/user/unlock',
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

  ngOnInit(): void {
    document.body.style.overflow = 'auto';
  }




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
    this.showAdminMenu = false;
    document.body.style.overflow = 'auto';
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
    this.showAdminMenu = !this.showAdminMenu;
  }

  /**
   * Navigates to the create news page
   */
  clickCreateNews() {
    this.router.navigate([this.routerLinks.createNews]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the create event page
   */
  clickCreateEvent() {
    this.router.navigate([this.routerLinks.createEvent]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the create location page
   */
  clickCreateLocation() {
    this.router.navigate([this.routerLinks.createLocation]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the add admin page
   */
  clickAddAdmin() {
    this.router.navigate([this.routerLinks.addAdmin]).then(r => this.showMobileMenu = false);
  }

  /**
   * Navigates to the unlock user page
   */
  clickUnlockUser() {
    this.router.navigate([this.routerLinks.unlockUser]).then(r => this.showMobileMenu = false);
  }
}
