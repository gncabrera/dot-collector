import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAttributeOption } from '../mega-attribute-option.model';

@Component({
  selector: 'jhi-mega-attribute-option-detail',
  templateUrl: './mega-attribute-option-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaAttributeOptionDetail {
  readonly megaAttributeOption = input<IMegaAttributeOption | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
