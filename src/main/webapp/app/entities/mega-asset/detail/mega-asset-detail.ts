import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAsset } from '../mega-asset.model';

@Component({
  selector: 'jhi-mega-asset-detail',
  templateUrl: './mega-asset-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class MegaAssetDetail {
  readonly megaAsset = input<IMegaAsset | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
