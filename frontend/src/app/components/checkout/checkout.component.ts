import { Component, OnInit } from '@angular/core';
import {Cart} from '../../dtos/cart';
import {CartService} from '../../services/cart.service';
import {ToastService} from '../../services/toast-service.service';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {

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
  error: string;


  constructor(private formBuilder: FormBuilder,
              private cartService: CartService,
              private toastService: ToastService,
              private router: Router) {
    this.checkoutForm = this.formBuilder.group({
      cardholder: ['', [Validators.required, Validators.minLength(2)]],
      cardnumber: ['', [Validators.required, Validators.minLength(16), Validators.pattern('^[0-9]*$')]],
      exp: ['', [Validators.required, Validators.minLength(5), Validators.pattern('^(0[1-9]|1[0-2])\\/?([0-9]{4}|[0-9]{2})$')]],
      csc: ['', [Validators.required, Validators.minLength(3), Validators.pattern('^[0-9]*$')]],
    });
  }

  ngOnInit(): void {
    this.loadCart();
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
}
