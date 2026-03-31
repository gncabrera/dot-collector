import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaSetPartCount } from '../mega-set-part-count.model';

@Component({
  selector: 'jhi-mega-set-part-count-detail',
  templateUrl: './mega-set-part-count-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaSetPartCountDetail {
  readonly megaSetPartCount = input<IMegaSetPartCount | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
