import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAttribute } from '../mega-attribute.model';
import { MegaAttributeService } from '../service/mega-attribute.service';

@Component({
  templateUrl: './mega-attribute-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaAttributeDeleteDialog {
  megaAttribute?: IMegaAttribute;

  protected readonly megaAttributeService = inject(MegaAttributeService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaAttributeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
