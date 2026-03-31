import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequestType } from '../profile-request-type.model';

@Component({
  selector: 'jhi-profile-request-type-detail',
  templateUrl: './profile-request-type-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ProfileRequestTypeDetail {
  readonly profileRequestType = input<IProfileRequestType | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
