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
import { IMegaSetType } from '../mega-set-type.model';
import { MegaSetTypeService } from '../service/mega-set-type.service';

import { MegaSetTypeFormGroup, MegaSetTypeFormService } from './mega-set-type-form.service';

@Component({
  selector: 'jhi-mega-set-type-update',
  templateUrl: './mega-set-type-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaSetTypeUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaSetType: IMegaSetType | null = null;

  megaAttributesSharedCollection = signal<IMegaAttribute[]>([]);

  protected megaSetTypeService = inject(MegaSetTypeService);
  protected megaSetTypeFormService = inject(MegaSetTypeFormService);
  protected megaAttributeService = inject(MegaAttributeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaSetTypeFormGroup = this.megaSetTypeFormService.createMegaSetTypeFormGroup();

  compareMegaAttribute = (o1: IMegaAttribute | null, o2: IMegaAttribute | null): boolean =>
    this.megaAttributeService.compareMegaAttribute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaSetType }) => {
      this.megaSetType = megaSetType;
      if (megaSetType) {
        this.updateForm(megaSetType);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaSetType = this.megaSetTypeFormService.getMegaSetType(this.editForm);
    if (megaSetType.id === null) {
      this.subscribeToSaveResponse(this.megaSetTypeService.create(megaSetType));
    } else {
      this.subscribeToSaveResponse(this.megaSetTypeService.update(megaSetType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaSetType | null>): void {
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

  protected updateForm(megaSetType: IMegaSetType): void {
    this.megaSetType = megaSetType;
    this.megaSetTypeFormService.resetForm(this.editForm, megaSetType);

    this.megaAttributesSharedCollection.update(megaAttributes =>
      this.megaAttributeService.addMegaAttributeToCollectionIfMissing<IMegaAttribute>(megaAttributes, ...(megaSetType.attributes ?? [])),
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
            ...(this.megaSetType?.attributes ?? []),
          ),
        ),
      )
      .subscribe((megaAttributes: IMegaAttribute[]) => this.megaAttributesSharedCollection.set(megaAttributes));
  }
}
