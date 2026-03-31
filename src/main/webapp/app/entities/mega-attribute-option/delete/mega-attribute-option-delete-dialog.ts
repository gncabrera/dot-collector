import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAttributeOption } from '../mega-attribute-option.model';
import { MegaAttributeOptionService } from '../service/mega-attribute-option.service';

@Component({
  templateUrl: './mega-attribute-option-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class MegaAttributeOptionDeleteDialog {
  megaAttributeOption?: IMegaAttributeOption;

  protected readonly megaAttributeOptionService = inject(MegaAttributeOptionService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.megaAttributeOptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
