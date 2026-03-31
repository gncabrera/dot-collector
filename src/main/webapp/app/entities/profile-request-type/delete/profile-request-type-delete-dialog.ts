import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequestType } from '../profile-request-type.model';
import { ProfileRequestTypeService } from '../service/profile-request-type.service';

@Component({
  templateUrl: './profile-request-type-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ProfileRequestTypeDeleteDialog {
  profileRequestType?: IProfileRequestType;

  protected readonly profileRequestTypeService = inject(ProfileRequestTypeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.profileRequestTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
