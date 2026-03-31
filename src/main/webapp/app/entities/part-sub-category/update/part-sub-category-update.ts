import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IPartSubCategory } from '../part-sub-category.model';
import { PartSubCategoryService } from '../service/part-sub-category.service';

import { PartSubCategoryFormGroup, PartSubCategoryFormService } from './part-sub-category-form.service';

@Component({
  selector: 'jhi-part-sub-category-update',
  templateUrl: './part-sub-category-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PartSubCategoryUpdate implements OnInit {
  readonly isSaving = signal(false);
  partSubCategory: IPartSubCategory | null = null;

  megaPartsSharedCollection = signal<IMegaPart[]>([]);

  protected partSubCategoryService = inject(PartSubCategoryService);
  protected partSubCategoryFormService = inject(PartSubCategoryFormService);
  protected megaPartService = inject(MegaPartService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PartSubCategoryFormGroup = this.partSubCategoryFormService.createPartSubCategoryFormGroup();

  compareMegaPart = (o1: IMegaPart | null, o2: IMegaPart | null): boolean => this.megaPartService.compareMegaPart(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ partSubCategory }) => {
      this.partSubCategory = partSubCategory;
      if (partSubCategory) {
        this.updateForm(partSubCategory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const partSubCategory = this.partSubCategoryFormService.getPartSubCategory(this.editForm);
    if (partSubCategory.id === null) {
      this.subscribeToSaveResponse(this.partSubCategoryService.create(partSubCategory));
    } else {
      this.subscribeToSaveResponse(this.partSubCategoryService.update(partSubCategory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPartSubCategory | null>): void {
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

  protected updateForm(partSubCategory: IPartSubCategory): void {
    this.partSubCategory = partSubCategory;
    this.partSubCategoryFormService.resetForm(this.editForm, partSubCategory);

    this.megaPartsSharedCollection.update(megaParts =>
      this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, ...(partSubCategory.megaParts ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaPartService
      .query()
      .pipe(map((res: HttpResponse<IMegaPart[]>) => res.body ?? []))
      .pipe(
        map((megaParts: IMegaPart[]) =>
          this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, ...(this.partSubCategory?.megaParts ?? [])),
        ),
      )
      .subscribe((megaParts: IMegaPart[]) => this.megaPartsSharedCollection.set(megaParts));
  }
}
