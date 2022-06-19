import { PagedUserDto, UserSearchDto } from './../../dtos/user';
import { ToastService } from './../../services/toast-service.service';
import { UserStatus } from './../../enums/user-status';
import { UserService } from './../../services/user.service';
import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/dtos/user';
import { FormBuilder, FormGroup } from '@angular/forms';
const FILTER_PAG_REGEX = /[^0-9]/g;

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {

  public user: User[];

  //Variables neeced for pagination
  public totalUsers = 25;
  public page = 1;
  public pageSize = 10;

  public userForm: FormGroup;

  constructor(private formBuilder: FormBuilder, private userService: UserService,
    private toastService: ToastService) {
    this.userForm = this.formBuilder.group({
      userRole: [''],
      userStatus: [''],
      userMail: ['']
    });
  }

  ngOnInit(): void {
    const userSearch: UserSearchDto = {
      status: this.userForm.controls.userStatus.value,
      role: this.userForm.controls.userRole.value,
      nameSearch: this.userForm.controls.userMail.value
    };

    this.loadUser(userSearch);
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, { classname: 'bg-danger text-light', delay: 5000 });
  }

  private loadUser(userSearch: UserSearchDto) {
    //Loads paginated Users
    this.userService.loadUser(userSearch, this.page - 1, this.pageSize).subscribe((data: PagedUserDto) => {
      if (data.totalCount === 0) {
        this.showDanger('No Users with matching criteria found!');
      }

      this.totalUsers = data.totalCount;
      this.user = data.users;

      debugger;
    }, (error) => {
      console.error(error);
      this.showDanger('Something went wrong!');
    });
  }

  //Handle switch to new page on pagination
  public handlePageChange() {
    const userSearch: UserSearchDto = {
      status: this.userForm.controls.userStatus.value,
      role: this.userForm.controls.userRole.value,
      nameSearch: this.userForm.controls.userMail.value
    };

    this.loadUser(userSearch);
  }

  public handleSearch() {
    const userSearch: UserSearchDto = {
      status: this.userForm.controls.userStatus.value,
      role: this.userForm.controls.userRole.value,
      nameSearch: this.userForm.controls.userMail.value
    };

    this.loadUser(userSearch);
  }

  public unlockUser(user: User) {
    this.userService.updateLockingState(user.id, false).subscribe((resp: User) => {
      // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
      const userStatus: UserStatus = (<any>UserStatus)[resp.status];

      if (userStatus === UserStatus.OK) {
        this.showSuccess(resp.email + ' unlocked!');
      }

      this.changeUserState(resp);
    }, (error) => {
      console.error(error);
      this.showDanger('Something went wrong!');
    });
  }

  public lockUser(user: User) {
    this.userService.updateLockingState(user.id, true).subscribe(
      (resp: User) => {
        // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
        const userStatus: UserStatus = (<any>UserStatus)[resp.status];

        if (userStatus === UserStatus.LOCKED) {
          this.showSuccess(resp.email + ' locked!');
        }

        this.changeUserState(resp);
      },
      (error) => {
        if (error.error.status === 403) {
          this.showDanger('Admins cannot lock themself out!');
        } else {
          this.showDanger('Something went wrong!');
        }
      });

  }

  //Changes from OK to LOCKED and otherwise
  private changeUserState(u: User) {
    for (let i = 0; i < this.user.length; i++) {
      if (this.user[i].id === u.id) {
        this.user[i] = u;
      }
    }
  }

  public selectPage(page: string) {
    this.page = parseInt(page, 10) || 1;
    this.handlePageChange();
  }

  public formatInput(input: HTMLInputElement) {
    input.value = input.value.replace(FILTER_PAG_REGEX, '');
  }
}
