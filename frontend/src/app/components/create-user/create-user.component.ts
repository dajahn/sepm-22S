import { Component, OnInit } from '@angular/core';
import {CountriesCodeToName} from '../../enums/countriesCodeToName';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {ToastService} from '../../services/toast-service.service';
import {CreateUpdateUser} from '../../dtos/user';
import {UserStatus} from '../../enums/user-status';
import {AuthRequest} from '../../dtos/auth-request';
import {BackendUserRoles} from '../../enums/backend-user-roles';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.scss']
})
export class CreateUserComponent implements OnInit {

  countriesCodeToName = CountriesCodeToName;
  countriesCodeToNameKeys = [];
  userRoles = BackendUserRoles;
  userRolesKeys = [];

  createUserForm: FormGroup;
  CreateUserFormMessages = {
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
    role: [
      {type: 'required', message: 'Role is required'}
    ],
  };


  constructor(private formBuilder: FormBuilder, private userService: UserService,
              private router: Router, private authService: AuthService, private toastService: ToastService) {
    this.createUserForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.minLength(2), Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      street: ['', [Validators.minLength(2), Validators.required]],
      zip: ['', [Validators.minLength(4), Validators.required]],
      city: ['', [Validators.minLength(2), Validators.required]],
      country: ['', [Validators.required]],
      passwordConfirm: ['', [Validators.required, Validators.minLength(8)]],
      role: [BackendUserRoles.CUSTOMER, [Validators.required]]
    });
    this.countriesCodeToNameKeys = Object.keys(this.countriesCodeToName);
    this.userRolesKeys = Object.keys(this.userRoles);
  }

  ngOnInit(): void {
  }

  registerUser() {
    const createU: CreateUpdateUser = {
      email: this.createUserForm.controls.email.value,
      password: this.createUserForm.controls.password.value,
      firstName: this.createUserForm.controls.firstName.value,
      lastName: this.createUserForm.controls.lastName.value,
      role: this.createUserForm.controls.role.value,
      address: {
        city: this.createUserForm.controls.city.value,
        street: this.createUserForm.controls.street.value,
        zipCode: this.createUserForm.controls.zip.value,
        country: this.createUserForm.controls.country.value
      },
      status: UserStatus.OK
    };
    this.userService.createUser(createU).subscribe({
      next: _ => {
        this.showSuccess('Successfully created Account!');
        this.router.navigate(['/']);
      },
      error: err => {
        this.showDanger(err.error.split('"')[1]);
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

}
