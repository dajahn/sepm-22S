import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ToastService } from '../../services/toast-service.service';
import { UserService } from '../../services/user.service';


@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  resetForm: FormGroup;
  loading = false;
  hash = undefined;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private toastService: ToastService,
    private userService: UserService,
    private route: ActivatedRoute
  ) {
    this.resetForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.hash = params.hash; // same as :username in route
    });
  }

  /**
   * Reset an users password
   */
  async resetPassword() {
    this.loading = true;
    const password = this.resetForm.controls.password.value;
    const hash = this.hash;

    this.userService.resetPassword(hash, password).subscribe({
      next: () => {
        this.toastService.show(`Successfully set new password. 🥳`, { classname: 'bg-success', delay: 5000 });
        this.router.navigate(['/login']);
      },
      error: error => {
        console.error(error);
        this.toastService.show(`Could not set the password 😢`, { classname: 'bg-danger', delay: 5000 });
      },
    });

    this.loading = false;

  }

}
