import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaSetType } from '../mega-set-type.model';

@Component({
  selector: 'jhi-mega-set-type-detail',
  templateUrl: './mega-set-type-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaSetTypeDetail {
  readonly megaSetType = input<IMegaSetType | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
