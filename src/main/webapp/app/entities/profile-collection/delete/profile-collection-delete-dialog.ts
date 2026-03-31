import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileCollection } from '../profile-collection.model';
import { ProfileCollectionService } from '../service/profile-collection.service';

@Component({
  templateUrl: './profile-collection-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ProfileCollectionDeleteDialog {
  profileCollection?: IProfileCollection;

  protected readonly profileCollectionService = inject(ProfileCollectionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.profileCollectionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
