import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IPartCategory } from '../part-category.model';
import { PartCategoryService } from '../service/part-category.service';

import { PartCategoryFormGroup, PartCategoryFormService } from './part-category-form.service';

@Component({
  selector: 'jhi-part-category-update',
  templateUrl: './part-category-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PartCategoryUpdate implements OnInit {
  readonly isSaving = signal(false);
  partCategory: IPartCategory | null = null;

  protected partCategoryService = inject(PartCategoryService);
  protected partCategoryFormService = inject(PartCategoryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PartCategoryFormGroup = this.partCategoryFormService.createPartCategoryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ partCategory }) => {
      this.partCategory = partCategory;
      if (partCategory) {
        this.updateForm(partCategory);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const partCategory = this.partCategoryFormService.getPartCategory(this.editForm);
    if (partCategory.id === null) {
      this.subscribeToSaveResponse(this.partCategoryService.create(partCategory));
    } else {
      this.subscribeToSaveResponse(this.partCategoryService.update(partCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPartCategory | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(partCategory: IPartCategory): void {
    this.partCategory = partCategory;
    this.partCategoryFormService.resetForm(this.editForm, partCategory);
  }
}
