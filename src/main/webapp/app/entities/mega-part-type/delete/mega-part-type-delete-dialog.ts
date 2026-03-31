import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaPartType } from '../mega-part-type.model';
import { MegaPartTypeService } from '../service/mega-part-type.service';

@Component({
  templateUrl: './mega-part-type-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaPartTypeDeleteDialog {
  megaPartType?: IMegaPartType;

  protected readonly megaPartTypeService = inject(MegaPartTypeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaPartTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
