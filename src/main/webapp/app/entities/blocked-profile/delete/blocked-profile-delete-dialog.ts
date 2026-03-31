import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IBlockedProfile } from '../blocked-profile.model';
import { BlockedProfileService } from '../service/blocked-profile.service';

@Component({
  templateUrl: './blocked-profile-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class BlockedProfileDeleteDialog {
  blockedProfile?: IBlockedProfile;

  protected readonly blockedProfileService = inject(BlockedProfileService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.blockedProfileService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
