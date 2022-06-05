import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {ToastService} from '../../services/toast-service.service';
import {CreateUpdateUser, User} from '../../dtos/user';
import {AuthRequest} from '../../dtos/auth-request';
import {CountriesCodeToName} from '../../enums/countriesCodeToName';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ErrorMessageParser} from '../../utils/error-message-parser';

@Component({
  selector: 'app-edit-account',
  templateUrl: './edit-account.component.html',
  styleUrls: ['./edit-account.component.scss']
})
export class EditAccountComponent implements OnInit {

  countriesCodeToName = CountriesCodeToName;
  countriesCodeToNameKeys = [];
  updateAccountForm: FormGroup;
  updateAccountFormMessages = {
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
  currUserData: User;

  constructor(private formBuilder: FormBuilder, private userService: UserService,
              private router: Router, private toastService: ToastService, private authService: AuthService,
              private modalService: NgbModal) {
    this.updateAccountForm = this.formBuilder.group({
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
    this.userService.getOwnData().subscribe({
      next: userData => {
        this.updateAccountForm.patchValue({
          firstName: userData.firstName,
          lastName: userData.lastName,
          email: userData.email,
          street: userData.address.street,
          zip: userData.address.zipCode,
          city: userData.address.city,
          country: userData.address.country
        });
        this.currUserData = userData;
      }, error: error => {
        this.showDanger(error.error.split('"')[1]);
      }
    });
  }

  updateUser() {
    const updateUser: CreateUpdateUser = {
      email: this.updateAccountForm.controls.email.value,
      password: this.updateAccountForm.controls.password.value,
      firstName: this.updateAccountForm.controls.firstName.value,
      lastName: this.updateAccountForm.controls.lastName.value,
      role: this.currUserData.role,
      address: {
        city: this.updateAccountForm.controls.city.value,
        street: this.updateAccountForm.controls.street.value,
        zipCode: this.updateAccountForm.controls.zip.value,
        country: this.updateAccountForm.controls.country.value
      },
      status: this.currUserData.status
    };
    this.userService.updateUser(updateUser, this.currUserData.id).subscribe({
      next: _ => {
        this.authService.logoutUser();
        this.authService.loginUser(new AuthRequest(updateUser.email, updateUser.password)).subscribe({
          next: __ => {
            this.showSuccess('Successfully updated Account!');
            this.router.navigate(['/']);
          }
        });
      },
      error: err => {
        this.showDanger(ErrorMessageParser.parseResponseToErrorMessage(err));
        this.error = ErrorMessageParser.parseResponseToErrorMessage(err);
      }
    });
  }

  showDeleteModal(deleteModal){
    this.modalService.open(deleteModal, { size: 'sm', centered: true });
  }

  deleteAccount() {
    this.userService.deleteUser().subscribe({
      next: () => {
        this.authService.logoutUser();
        this.showSuccess('Successfully deleted user.');
        this.router.navigate(['/register']);
      },
      error: err => {
        console.log('Error while trying to delete user.', err);
        this.showDanger('Unable to delete user.');
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
