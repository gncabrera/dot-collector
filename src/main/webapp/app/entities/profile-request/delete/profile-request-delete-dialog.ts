import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequest } from '../profile-request.model';
import { ProfileRequestService } from '../service/profile-request.service';

@Component({
  templateUrl: './profile-request-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ProfileRequestDeleteDialog {
  profileRequest?: IProfileRequest;

  protected readonly profileRequestService = inject(ProfileRequestService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.profileRequestService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
