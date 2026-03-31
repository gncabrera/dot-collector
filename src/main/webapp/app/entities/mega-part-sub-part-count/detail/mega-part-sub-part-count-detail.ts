import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';

@Component({
  selector: 'jhi-mega-part-sub-part-count-detail',
  templateUrl: './mega-part-sub-part-count-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaPartSubPartCountDetail {
  readonly megaPartSubPartCount = input<IMegaPartSubPartCount | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
