import { Component, OnInit } from '@angular/core';
import {Cart} from '../../dtos/cart';
import {CartService} from '../../services/cart.service';
import {ToastService} from '../../services/toast-service.service';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CheckoutService} from '../../services/checkout.service';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {Checkout} from '../../dtos/checkout';
import {CountriesCodeToName} from '../../enums/countriesCodeToName';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {

  currUserData: User;
  cart: Cart;
  editAddress = false;
  countriesCodeToName = CountriesCodeToName;
  countriesCodeToNameKeys = [];

  checkoutForm: FormGroup;
  checkoutFormMessages = {
    cardholder: [
      {type: 'required', message: 'Cardholder\'s name is required'},
      {type: 'minlength', message: 'Cardholder\'s name must contain at least 2 characters'}
    ],
    cardnumber: [
      {type: 'required', message: 'Card number is required'},
      {type: 'minlength', message: 'Card number must contain exactly 16 numbers'},
      {type: 'pattern', message: 'Card number must be numeric'}
    ],
    exp: [
      {type: 'required', message: 'Expiry date is required'},
      {type: 'pattern', message: 'Expiry date must follow the pattern MM/YY'}
    ],
    csc: [
      {type: 'required', message: 'CVV or CVC is required'},
      {type: 'minlength', message: 'CVV or CVC must contain at least 3 numbers'},
      {type: 'pattern', message: 'Card number must be numeric'}
    ]
  };

  editAddressForm: FormGroup;
  editAddressFormMessages = {
    editStreet: [
      {type: 'required', message: 'Street is required'},
      {type: 'minlength', message: 'Street must contain at least 2 characters'}
    ],
    editZip: [
      {type: 'required', message: 'ZIP Code is required'},
      {type: 'minlength', message: 'ZIP Code must contain at least 4 characters'}
    ],
    editCity: [
      {type: 'required', message: 'City is required'},
      {type: 'minlength', message: 'City must contain at least 2 characters'}
    ],
    editCountry: [
      {type: 'required', message: 'Country is required'}
    ],
  };

  error: string;



  constructor(private formBuilder: FormBuilder,
              private cartService: CartService,
              private toastService: ToastService,
              private router: Router,
              private userService: UserService,
              private checkoutService: CheckoutService) {
    this.checkoutForm = this.formBuilder.group({
      cardholder: ['', [Validators.required, Validators.minLength(2)]],
      cardnumber: ['', [Validators.required, Validators.minLength(16), Validators.pattern('^[0-9]*$')]],
      exp: ['', [Validators.required, Validators.minLength(5), Validators.pattern('^(0[1-9]|1[0-2])\\/?([0-9]{4}|[0-9]{2})$')]],
      csc: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[0-9]*$')]],
    });

    this.editAddressForm = this.formBuilder.group({
      editStreet: ['', [Validators.minLength(2), Validators.required]],
      editZip: ['', [Validators.minLength(4), Validators.required]],
      editCity: ['', [Validators.minLength(2), Validators.required]],
      editCountry: ['', [Validators.required]],
    });

    this.countriesCodeToNameKeys = Object.keys(this.countriesCodeToName);
  }

  ngOnInit(): void {
    this.loadCart();
    this.loadUser();
  }



  /**
   * Loads cart order
   */
  private loadCart() {
    this.cartService.getCart().subscribe({
      next: (cart: Cart) => {
        if(cart?.tickets.length === 0) {
          console.log('Cart is empty!');
          this.showDanger('Your cart is empty, nothing to checkout!');
          this.router.navigate(['/cart']).then();
        } else {
          this.cart = cart;
        }
      },
      error: err => {
        console.error('Error fetching cart', err);
        this.showDanger('Sorry, something went wrong. Could not load the cart 😔 Please try again later!');
        this.router.navigate(['/cart']).then();
      }
    });
  }

  /**
   * Loads the currently logged-in user
   */
  private loadUser(){
    this.userService.getOwnData().subscribe({
      next: userData => {
        this.currUserData = userData;
        this.editAddressForm.controls.editStreet.setValue(userData.address.street);
        this.editAddressForm.controls.editZip.setValue(userData.address.zipCode);
        this.editAddressForm.controls.editCity.setValue(userData.address.city);
        this.editAddressForm.controls.editCountry.setValue(userData.address.country);
      }, error: err => {
        console.error('Error fetching user', err);
        this.showDanger('Sorry, something went wrong. Could not load the user data 😔 Please try again later!');
        this.router.navigate(['/cart']).then();
      }
    });
  }

  /**
   * Checks out the cart of the currently logged-in user.
   */
  confirm() {
    const checkout: Checkout = {
      cardholder: this.checkoutForm.controls.cardholder.value,
      cardnumber: this.checkoutForm.controls.cardnumber.value,
      exp: this.checkoutForm.controls.exp.value,
      csc: this.checkoutForm.controls.csc.value
    };

    this.checkoutService.checkout(checkout).subscribe({
      next: () => {
        console.log('Successfully checked out cart!');
        this.showSuccess('Successfully checked out cart 🎉');
        this.router.navigate(['/cart']).then();
      },
      error: err => {
        console.error('Error checking out cart', err);
        this.showDanger(err.error.split('"')[1]);
        this.error = err.error.message;
      }
    });
  }

  /**
   * Adds a '/' automatically to the exp date
   */
  autoSlash(e) {
    if (e.key === '/') {
      e.preventDefault();
    }
    if (e.key === 'Backspace' || e.key === 'Delete') {
      if (this.checkoutForm.value.exp.length === 3) {
        this.checkoutForm.controls.exp.setValue(this.checkoutForm.value.exp.substring(0, 2));
      } else {
        return;
      }
    }
    if (this.checkoutForm.value.exp.length === 2) {
      this.checkoutForm.controls.exp.setValue(this.checkoutForm.value.exp + '/');
    }
  }

  /**
   * Calculates the total price of all tickets combined
   */
  calculateTotalSum(): number{
    return this.cart?.tickets.reduce((accumulator, current) => accumulator + current?.sector.price, 0);
  }

  /**
   * Displays message on a success.
   */
  showSuccess(msg: string) {
    this.toastService.show(msg, {classname: 'bg-success', delay: 3000});
  }

  /**
   * Displays message on a failure.
   */
  showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger', delay: 5000});
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = null;
  }

  /**
   * Applies new shipping address
   */
  newAddress() {
    this.currUserData.address.street = this.editAddressForm.value.editStreet;
    this.currUserData.address.zipCode = this.editAddressForm.value.editZip;
    this.currUserData.address.city = this.editAddressForm.value.editCity;
    this.currUserData.address.country = this.editAddressForm.value.editCountry;
    this.editAddress = false;
  }
}
