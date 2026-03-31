import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IProfileCollectionSet } from '../profile-collection-set.model';

@Component({
  selector: 'jhi-profile-collection-set-detail',
  templateUrl: './profile-collection-set-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatePipe],
})
export class ProfileCollectionSetDetail {
  readonly profileCollectionSet = input<IProfileCollectionSet | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
