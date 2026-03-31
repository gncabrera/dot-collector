import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IFollowingProfile } from '../following-profile.model';
import { FollowingProfileService } from '../service/following-profile.service';

@Component({
  templateUrl: './following-profile-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class FollowingProfileDeleteDialog {
  followingProfile?: IFollowingProfile;

  protected readonly followingProfileService = inject(FollowingProfileService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.followingProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
