import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import { MegaPartSubPartCountService } from '../service/mega-part-sub-part-count.service';

@Component({
  templateUrl: './mega-part-sub-part-count-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaPartSubPartCountDeleteDialog {
  megaPartSubPartCount?: IMegaPartSubPartCount;

  protected readonly megaPartSubPartCountService = inject(MegaPartSubPartCountService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaPartSubPartCountService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
