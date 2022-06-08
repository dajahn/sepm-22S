import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LocationService } from '../../services/location.service';
import { ToastService } from '../../services/toast-service.service';
import { Location } from '../../dtos/location';
import { CountriesCodeToName } from '../../enums/countriesCodeToName';

@Component({
  selector: 'app-location-create',
  templateUrl: './location-create.component.html',
  styleUrls: ['./location-create.component.scss']
})
export class LocationCreateComponent implements OnInit {

  public countriesCodeToName = CountriesCodeToName;
  public countriesCodeToNameKeys = [];

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
      street: ['', [Validators.required, Validators.minLength(2)]],
      zip: ['', [Validators.required, Validators.minLength(4)]],
      city: ['', [Validators.required, Validators.minLength(2)]],
      country: ['', [Validators.required]],
    }, { updateOn: 'change' } );


    this.location = {
      id: null,
      name: '',
      address: null,
      sectors: [],
    };

    this.form.valueChanges.subscribe((value) => {
      const { name, street, zip: zipCode, city, country } = value;
      console.log('lol', value);

      this.location.name = name;
      this.location.address = {
        street,
        zipCode,
        city,
        country,
      };

      console.log(this.location);
    });

    this.countriesCodeToNameKeys = Object.keys(this.countriesCodeToName);
  }

  ngOnInit(): void {
  }

  save() {

    if (!this.form.valid) {
      return;
    }

    this.locationService.save(this.location).subscribe({
      next: () => {
        console.log(`Location created with name '${this.location.name}'!`);
        this.showSuccess(`Location created with name '${this.location.name}'  🎉!`);
      },
      error: err => {
        console.error('Error creating location', err);
        this.showDanger(( err.error?.error || err.error?.split('"')[1] ) ?? 'Unknown error while creating location!');
      }
    });
  }

  showFormErrors( target: string ) {
    const item = this.form.get([ target ]);
    return ( item.dirty || item.touched ) && item.errors;
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
