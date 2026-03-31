import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IBlockedProfile } from '../blocked-profile.model';

@Component({
  selector: 'jhi-blocked-profile-detail',
  templateUrl: './blocked-profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatePipe],
})
export class BlockedProfileDetail {
  readonly blockedProfile = input<IBlockedProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
