import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { MegaPartTypeService } from 'app/entities/mega-part-type/service/mega-part-type.service';
import { IPartCategory } from 'app/entities/part-category/part-category.model';
import { PartCategoryService } from 'app/entities/part-category/service/part-category.service';
import { IPartSubCategory } from 'app/entities/part-sub-category/part-sub-category.model';
import { PartSubCategoryService } from 'app/entities/part-sub-category/service/part-sub-category.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { IMegaPart } from '../mega-part.model';
import { MegaPartService } from '../service/mega-part.service';

import { MegaPartFormGroup, MegaPartFormService } from './mega-part-form.service';

@Component({
  selector: 'jhi-mega-part-update',
  templateUrl: './mega-part-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class MegaPartUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaPart: IMegaPart | null = null;

  megaPartTypesSharedCollection = signal<IMegaPartType[]>([]);
  partCategoriesSharedCollection = signal<IPartCategory[]>([]);
  partSubCategoriesSharedCollection = signal<IPartSubCategory[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected megaPartService = inject(MegaPartService);
  protected megaPartFormService = inject(MegaPartFormService);
  protected megaPartTypeService = inject(MegaPartTypeService);
  protected partCategoryService = inject(PartCategoryService);
  protected partSubCategoryService = inject(PartSubCategoryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaPartFormGroup = this.megaPartFormService.createMegaPartFormGroup();

  compareMegaPartType = (o1: IMegaPartType | null, o2: IMegaPartType | null): boolean =>
    this.megaPartTypeService.compareMegaPartType(o1, o2);

  comparePartCategory = (o1: IPartCategory | null, o2: IPartCategory | null): boolean =>
    this.partCategoryService.comparePartCategory(o1, o2);

  comparePartSubCategory = (o1: IPartSubCategory | null, o2: IPartSubCategory | null): boolean =>
    this.partSubCategoryService.comparePartSubCategory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaPart }) => {
      this.megaPart = megaPart;
      if (megaPart) {
        this.updateForm(megaPart);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('dotCollectorApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaPart = this.megaPartFormService.getMegaPart(this.editForm);
    if (megaPart.id === null) {
      this.subscribeToSaveResponse(this.megaPartService.create(megaPart));
    } else {
      this.subscribeToSaveResponse(this.megaPartService.update(megaPart));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaPart | null>): void {
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

  protected updateForm(megaPart: IMegaPart): void {
    this.megaPart = megaPart;
    this.megaPartFormService.resetForm(this.editForm, megaPart);

    this.megaPartTypesSharedCollection.update(megaPartTypes =>
      this.megaPartTypeService.addMegaPartTypeToCollectionIfMissing<IMegaPartType>(megaPartTypes, megaPart.type),
    );
    this.partCategoriesSharedCollection.update(partCategories =>
      this.partCategoryService.addPartCategoryToCollectionIfMissing<IPartCategory>(partCategories, megaPart.partCategory),
    );
    this.partSubCategoriesSharedCollection.update(partSubCategories =>
      this.partSubCategoryService.addPartSubCategoryToCollectionIfMissing<IPartSubCategory>(
        partSubCategories,
        ...(megaPart.partSubCategories ?? []),
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaPartTypeService
      .query()
      .pipe(map((res: HttpResponse<IMegaPartType[]>) => res.body ?? []))
      .pipe(
        map((megaPartTypes: IMegaPartType[]) =>
          this.megaPartTypeService.addMegaPartTypeToCollectionIfMissing<IMegaPartType>(megaPartTypes, this.megaPart?.type),
        ),
      )
      .subscribe((megaPartTypes: IMegaPartType[]) => this.megaPartTypesSharedCollection.set(megaPartTypes));

    this.partCategoryService
      .query()
      .pipe(map((res: HttpResponse<IPartCategory[]>) => res.body ?? []))
      .pipe(
        map((partCategories: IPartCategory[]) =>
          this.partCategoryService.addPartCategoryToCollectionIfMissing<IPartCategory>(partCategories, this.megaPart?.partCategory),
        ),
      )
      .subscribe((partCategories: IPartCategory[]) => this.partCategoriesSharedCollection.set(partCategories));

    this.partSubCategoryService
      .query()
      .pipe(map((res: HttpResponse<IPartSubCategory[]>) => res.body ?? []))
      .pipe(
        map((partSubCategories: IPartSubCategory[]) =>
          this.partSubCategoryService.addPartSubCategoryToCollectionIfMissing<IPartSubCategory>(
            partSubCategories,
            ...(this.megaPart?.partSubCategories ?? []),
          ),
        ),
      )
      .subscribe((partSubCategories: IPartSubCategory[]) => this.partSubCategoriesSharedCollection.set(partSubCategories));
  }
}
