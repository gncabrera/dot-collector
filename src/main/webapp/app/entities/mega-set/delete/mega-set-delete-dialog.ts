import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaSet } from '../mega-set.model';
import { MegaSetService } from '../service/mega-set.service';

@Component({
  templateUrl: './mega-set-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaSetDeleteDialog {
  megaSet?: IMegaSet;

  protected readonly megaSetService = inject(MegaSetService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaSetService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
