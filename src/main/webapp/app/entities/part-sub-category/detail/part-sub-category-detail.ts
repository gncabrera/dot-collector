import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPartSubCategory } from '../part-sub-category.model';

@Component({
  selector: 'jhi-part-sub-category-detail',
  templateUrl: './part-sub-category-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class PartSubCategoryDetail {
  readonly partSubCategory = input<IPartSubCategory | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
