import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileCollectionSet } from '../profile-collection-set.model';
import { ProfileCollectionSetService } from '../service/profile-collection-set.service';

@Component({
  templateUrl: './profile-collection-set-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class ProfileCollectionSetDeleteDialog {
  profileCollectionSet?: IProfileCollectionSet;

  protected readonly profileCollectionSetService = inject(ProfileCollectionSetService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.profileCollectionSetService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
