import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfile } from '../profile.model';

@Component({
  selector: 'jhi-profile-detail',
  templateUrl: './profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ProfileDetail {
  readonly profile = input<IProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
