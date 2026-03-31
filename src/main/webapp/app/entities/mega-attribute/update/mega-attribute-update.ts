import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AttributeType } from 'app/entities/enumerations/attribute-type.model';
import { UIComponent } from 'app/entities/enumerations/ui-component.model';
import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { MegaPartTypeService } from 'app/entities/mega-part-type/service/mega-part-type.service';
import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';
import { MegaSetTypeService } from 'app/entities/mega-set-type/service/mega-set-type.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAttribute } from '../mega-attribute.model';
import { MegaAttributeService } from '../service/mega-attribute.service';

import { MegaAttributeFormGroup, MegaAttributeFormService } from './mega-attribute-form.service';

@Component({
  selector: 'jhi-mega-attribute-update',
  templateUrl: './mega-attribute-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaAttributeUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaAttribute: IMegaAttribute | null = null;
  uIComponentValues = Object.keys(UIComponent);
  attributeTypeValues = Object.keys(AttributeType);

  megaSetTypesSharedCollection = signal<IMegaSetType[]>([]);
  megaPartTypesSharedCollection = signal<IMegaPartType[]>([]);

  protected megaAttributeService = inject(MegaAttributeService);
  protected megaAttributeFormService = inject(MegaAttributeFormService);
  protected megaSetTypeService = inject(MegaSetTypeService);
  protected megaPartTypeService = inject(MegaPartTypeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaAttributeFormGroup = this.megaAttributeFormService.createMegaAttributeFormGroup();

  compareMegaSetType = (o1: IMegaSetType | null, o2: IMegaSetType | null): boolean => this.megaSetTypeService.compareMegaSetType(o1, o2);

  compareMegaPartType = (o1: IMegaPartType | null, o2: IMegaPartType | null): boolean =>
    this.megaPartTypeService.compareMegaPartType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaAttribute }) => {
      this.megaAttribute = megaAttribute;
      if (megaAttribute) {
        this.updateForm(megaAttribute);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaAttribute = this.megaAttributeFormService.getMegaAttribute(this.editForm);
    if (megaAttribute.id === null) {
      this.subscribeToSaveResponse(this.megaAttributeService.create(megaAttribute));
    } else {
      this.subscribeToSaveResponse(this.megaAttributeService.update(megaAttribute));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaAttribute | null>): void {
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

  protected updateForm(megaAttribute: IMegaAttribute): void {
    this.megaAttribute = megaAttribute;
    this.megaAttributeFormService.resetForm(this.editForm, megaAttribute);

    this.megaSetTypesSharedCollection.update(megaSetTypes =>
      this.megaSetTypeService.addMegaSetTypeToCollectionIfMissing<IMegaSetType>(megaSetTypes, ...(megaAttribute.setTypes ?? [])),
    );
    this.megaPartTypesSharedCollection.update(megaPartTypes =>
      this.megaPartTypeService.addMegaPartTypeToCollectionIfMissing<IMegaPartType>(megaPartTypes, ...(megaAttribute.partTypes ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaSetTypeService
      .query()
      .pipe(map((res: HttpResponse<IMegaSetType[]>) => res.body ?? []))
      .pipe(
        map((megaSetTypes: IMegaSetType[]) =>
          this.megaSetTypeService.addMegaSetTypeToCollectionIfMissing<IMegaSetType>(megaSetTypes, ...(this.megaAttribute?.setTypes ?? [])),
        ),
      )
      .subscribe((megaSetTypes: IMegaSetType[]) => this.megaSetTypesSharedCollection.set(megaSetTypes));

    this.megaPartTypeService
      .query()
      .pipe(map((res: HttpResponse<IMegaPartType[]>) => res.body ?? []))
      .pipe(
        map((megaPartTypes: IMegaPartType[]) =>
          this.megaPartTypeService.addMegaPartTypeToCollectionIfMissing<IMegaPartType>(
            megaPartTypes,
            ...(this.megaAttribute?.partTypes ?? []),
          ),
        ),
      )
      .subscribe((megaPartTypes: IMegaPartType[]) => this.megaPartTypesSharedCollection.set(megaPartTypes));
  }
}
