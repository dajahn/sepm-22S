import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {CreateUpdateUser} from '../../dtos/user';
import {UserStatus} from '../../enums/user-status';
import {UserService} from '../../services/user.service';
import {AuthRequest} from '../../dtos/auth-request';
import {ToastService} from '../../services/toast-service.service';
import {CountriesCodeToName} from '../../enums/countriesCodeToName';
import {BackendUserRoles} from '../../enums/backend-user-roles';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  countriesCodeToName = CountriesCodeToName;
  countriesCodeToNameKeys = [];

  registerForm: FormGroup;
  registerFormMessages = {
    firstName: [
      {type: 'required', message: 'Firstname is required'},
      {type: 'minlength', message: 'Firstname must contain at least 2 characters'}
    ],
    lastName: [
      {type: 'required', message: 'Lastname is required'},
      {type: 'minlength', message: 'Firstname must contain at least 2 characters'}
    ],
    email: [
      {type: 'required', message: 'Email is required'}
    ],
    password: [
      {type: 'required', message: 'Password is required'},
      {type: 'match', message: 'Passwords must match'}
    ],
    passwordConfirm: [
      {type: 'required', message: 'Password confirmation is required'}
    ],
    street: [
      {type: 'required', message: 'Street is required'},
      {type: 'minlength', message: 'Street must contain at least 2 characters'}
    ],
    zip: [
      {type: 'required', message: 'ZIP Code is required'},
      {type: 'minlength', message: 'ZIP Code must contain at least 4 characters'}
    ],
    city: [
      {type: 'required', message: 'City is required'},
      {type: 'minlength', message: 'City must contain at least 2 characters'}
    ],
    country: [
      {type: 'required', message: 'Country is required'}
    ],
  };
  error: string;

  constructor(private formBuilder: FormBuilder, private userService: UserService,
              private router: Router, private authService: AuthService, private toastService: ToastService) {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.minLength(2), Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      street: ['', [Validators.minLength(2), Validators.required]],
      zip: ['', [Validators.minLength(4), Validators.required]],
      city: ['', [Validators.minLength(2), Validators.required]],
      country: ['', [Validators.required]],
      passwordConfirm: ['', [Validators.required, Validators.minLength(8)]]
    });
    this.countriesCodeToNameKeys = Object.keys(this.countriesCodeToName);
  }

  ngOnInit(): void {
  }

  registerUser() {
    const createU: CreateUpdateUser = {
      email: this.registerForm.controls.email.value,
      password: this.registerForm.controls.password.value,
      firstName: this.registerForm.controls.firstName.value,
      lastName: this.registerForm.controls.lastName.value,
      role: BackendUserRoles.CUSTOMER,
      address: {
        city: this.registerForm.controls.city.value,
        street: this.registerForm.controls.street.value,
        zipCode: this.registerForm.controls.zip.value,
        country: this.registerForm.controls.country.value
      },
      status: UserStatus.OK
    };
    this.userService.createUser(createU).subscribe({
      next: value => {
        this.showSuccess('Successfully created Account!');
        this.authService.loginUser(new AuthRequest(createU.email, createU.password)).subscribe({
          next: _ => {
            this.router.navigate(['/message']);
          }
        });

      },
      error: err => {
        this.showDanger(err.error.split('"')[1]);
        this.error = err.error.message;
      }
    });
  }


  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, {classname: 'bg-danger text-light', delay: 10000});
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = null;
  }
}
