import {Component, OnInit, TemplateRef} from '@angular/core';
import {ToastService} from '../../services/toast-service.service';

@Component({
  selector: 'app-toasts',
  templateUrl: './toast-component.component.html',
  styleUrls: ['./toast-component.component.scss'],
  // eslint-disable-next-line @angular-eslint/no-host-metadata-property
  host: {class: 'toast-container position-fixed top-0 end-0 p-3', style: 'z-index: 1200'}
})
export class ToastComponentComponent implements OnInit {

  constructor(public toastService: ToastService) {
  }

  isTemplate(toast) {
    return toast.textOrTpl instanceof TemplateRef;
  }

  ngOnInit(): void {
  }

}
