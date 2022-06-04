import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LocationService } from '../../services/location.service';
import { ToastService } from '../../services/toast-service.service';
import { Location } from '../../dtos/location';

@Component({
  selector: 'app-location-create',
  templateUrl: './location-create.component.html',
  styleUrls: ['./location-create.component.scss']
})
export class LocationCreateComponent implements OnInit {

  public form: FormGroup;

  public state: FormState = FormState.DEFAULT;
  public stage: FormStage = FormStage.DATA;

  public location: Location = null;

  // enum mapping
  public FormState = FormState;
  public FormStage = FormStage;

  constructor(
    private formBuilder: FormBuilder,
    private locationService: LocationService,
    private toastService: ToastService
  ) {
    this.form = this.formBuilder.group({
      name: ['', [Validators.required, Validators.maxLength(255)]],
    });
  }

  ngOnInit(): void {
  }

  save() {
    this.locationService.save(this.location).subscribe({
      next: () => {
        console.log(`Location created with name '${this.location.name}'!`);
        this.showSuccess(`Location created with name '${this.location.name}'  🎉!`);
      },
      error: err => {
        console.error('Error creating location', err);
        this.showDanger(err.error?.split('"')[1] ?? 'Unknown error while creating location!');
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

}

enum FormState {
  DEFAULT,
  LOADING,
  SUCCESS,
  ERROR,
}

enum FormStage {
  DATA,
  GRAPHICAL,
}
