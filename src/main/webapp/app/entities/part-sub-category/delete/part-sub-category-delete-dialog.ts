import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPartSubCategory } from '../part-sub-category.model';
import { PartSubCategoryService } from '../service/part-sub-category.service';

@Component({
  templateUrl: './part-sub-category-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class PartSubCategoryDeleteDialog {
  partSubCategory?: IPartSubCategory;

  protected readonly partSubCategoryService = inject(PartSubCategoryService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.partSubCategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
