import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

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
    topTen: '/topten',
    cart: '/cart',
    reservations: '/reservations',
    adminTools: '/admintools'
  };



  constructor(public authService: AuthService) { }

  ngOnInit(): void {}




  /**
   * Shows and hides the mobile extended navigation
   * Disables scroll on the menu is shown
   */
  openMobileMenu() {
    // disables scrollbar when mobile menu is expanded
    if(this.showMobileMenu){
      document.body.style.overflow = 'auto';
    } else {
      document.body.style.overflow = 'hidden';
    }
    this.showMobileMenu = !this.showMobileMenu;
  }

}
