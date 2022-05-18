import { ToastService } from './../../services/toast-service.service';
import { UserStatus } from './../../enums/user-status';
import { UserService } from './../../services/user.service';
import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/dtos/user';


@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss']
})
export class UserManagementComponent implements OnInit {

  public user: User[];

  constructor(private userService: UserService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.loadAllUser();
  }

  private showSuccess(msg: string) {
    this.toastService.show(msg, {
      classname: 'bg-success text-light', delay: 3000
    });
  }

  private showDanger(msg: string) {
    this.toastService.show(msg, { classname: 'bg-danger text-light', delay: 5000 });
  }

  private loadAllUser() {
    //TODO set self gray or non clickable
    this.userService.loadAllUser().subscribe((data: User[]) => {
      this.user = data;
    })
  }

  public unlockUser(user: User) {
    this.userService.unlockUser(user.id).subscribe((resp: User) => {
      let userStatus: UserStatus = (<any>UserStatus)[resp.status];

      if (userStatus == UserStatus.OK)
        this.showSuccess(resp.email + " unlocked!");

      this.changeUserState(resp);
    })
  }

  public lockUser(user: User) {
    this.userService.lockUser(user.id).subscribe(
      (resp: User) => {
        let userStatus: UserStatus = (<any>UserStatus)[resp.status];

        if (userStatus == UserStatus.LOCKED)
          this.showSuccess(resp.email + " locked!");

        this.changeUserState(resp);
      },
      (error) => {
        if (error.error.status == 403)
          this.showDanger("Admins cannot lock themself out!");
        else
          this.showDanger("Something went wrong!");
      })
  }

  //Changes from OK to LOCKED and otherwise
  private changeUserState(u: User) {
    for (let i = 0; i < this.user.length; i++) {
      if (this.user[i].id == u.id)
        this.user[i] = u;
    }
  }

}
