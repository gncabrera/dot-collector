import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAttribute } from '../mega-attribute.model';

@Component({
  selector: 'jhi-mega-attribute-detail',
  templateUrl: './mega-attribute-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaAttributeDetail {
  readonly megaAttribute = input<IMegaAttribute | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
