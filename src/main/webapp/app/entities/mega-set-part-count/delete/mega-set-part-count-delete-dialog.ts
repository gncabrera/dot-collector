import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { MegaSetPartCountService } from '../service/mega-set-part-count.service';

@Component({
  templateUrl: './mega-set-part-count-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaSetPartCountDeleteDialog {
  megaSetPartCount?: IMegaSetPartCount;

  protected readonly megaSetPartCountService = inject(MegaSetPartCountService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaSetPartCountService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
