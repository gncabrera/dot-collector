import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPartCategory } from '../part-category.model';

@Component({
  selector: 'jhi-part-category-detail',
  templateUrl: './part-category-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class PartCategoryDetail {
  readonly partCategory = input<IPartCategory | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
