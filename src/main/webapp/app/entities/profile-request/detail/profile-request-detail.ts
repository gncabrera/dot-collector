import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequest } from '../profile-request.model';

@Component({
  selector: 'jhi-profile-request-detail',
  templateUrl: './profile-request-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ProfileRequestDetail {
  readonly profileRequest = input<IProfileRequest | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
