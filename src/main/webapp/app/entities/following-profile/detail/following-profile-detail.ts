import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IFollowingProfile } from '../following-profile.model';

@Component({
  selector: 'jhi-following-profile-detail',
  templateUrl: './following-profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink, FormatMediumDatePipe],
})
export class FollowingProfileDetail {
  readonly followingProfile = input<IFollowingProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
