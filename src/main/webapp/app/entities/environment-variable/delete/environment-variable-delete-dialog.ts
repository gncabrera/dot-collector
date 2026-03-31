import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';

import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { AlertError } from 'app/shared/alert/alert-error';
import { IEnvironmentVariable } from '../environment-variable.model';
import { EnvironmentVariableService } from '../service/environment-variable.service';

@Component({
  templateUrl: './environment-variable-delete-dialog.html',
  imports: [FormsModule, FontAwesomeModule, AlertError],
})
export class EnvironmentVariableDeleteDialog {
  environmentVariable?: IEnvironmentVariable;

  protected readonly environmentVariableService = inject(EnvironmentVariableService);
  protected readonly activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.environmentVariableService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
