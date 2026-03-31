import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileCollection } from '../profile-collection.model';

@Component({
  selector: 'jhi-profile-collection-detail',
  templateUrl: './profile-collection-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class ProfileCollectionDetail {
  readonly profileCollection = input<IProfileCollection | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
