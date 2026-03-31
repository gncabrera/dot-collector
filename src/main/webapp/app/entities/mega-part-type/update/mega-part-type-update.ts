import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';
import { MegaAttributeService } from 'app/entities/mega-attribute/service/mega-attribute.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaPartType } from '../mega-part-type.model';
import { MegaPartTypeService } from '../service/mega-part-type.service';

import { MegaPartTypeFormGroup, MegaPartTypeFormService } from './mega-part-type-form.service';

@Component({
  selector: 'jhi-mega-part-type-update',
  templateUrl: './mega-part-type-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaPartTypeUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaPartType: IMegaPartType | null = null;

  megaAttributesSharedCollection = signal<IMegaAttribute[]>([]);

  protected megaPartTypeService = inject(MegaPartTypeService);
  protected megaPartTypeFormService = inject(MegaPartTypeFormService);
  protected megaAttributeService = inject(MegaAttributeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaPartTypeFormGroup = this.megaPartTypeFormService.createMegaPartTypeFormGroup();

  compareMegaAttribute = (o1: IMegaAttribute | null, o2: IMegaAttribute | null): boolean =>
    this.megaAttributeService.compareMegaAttribute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaPartType }) => {
      this.megaPartType = megaPartType;
      if (megaPartType) {
        this.updateForm(megaPartType);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaPartType = this.megaPartTypeFormService.getMegaPartType(this.editForm);
    if (megaPartType.id === null) {
      this.subscribeToSaveResponse(this.megaPartTypeService.create(megaPartType));
    } else {
      this.subscribeToSaveResponse(this.megaPartTypeService.update(megaPartType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaPartType | null>): void {
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

  protected updateForm(megaPartType: IMegaPartType): void {
    this.megaPartType = megaPartType;
    this.megaPartTypeFormService.resetForm(this.editForm, megaPartType);

    this.megaAttributesSharedCollection.update(megaAttributes =>
      this.megaAttributeService.addMegaAttributeToCollectionIfMissing<IMegaAttribute>(megaAttributes, ...(megaPartType.attributes ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaAttributeService
      .query()
      .pipe(map((res: HttpResponse<IMegaAttribute[]>) => res.body ?? []))
      .pipe(
        map((megaAttributes: IMegaAttribute[]) =>
          this.megaAttributeService.addMegaAttributeToCollectionIfMissing<IMegaAttribute>(
            megaAttributes,
            ...(this.megaPartType?.attributes ?? []),
          ),
        ),
      )
      .subscribe((megaAttributes: IMegaAttribute[]) => this.megaAttributesSharedCollection.set(megaAttributes));
  }
}
