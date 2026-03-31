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
import { IMegaAttributeOption } from '../mega-attribute-option.model';
import { MegaAttributeOptionService } from '../service/mega-attribute-option.service';

import { MegaAttributeOptionFormGroup, MegaAttributeOptionFormService } from './mega-attribute-option-form.service';

@Component({
  selector: 'jhi-mega-attribute-option-update',
  templateUrl: './mega-attribute-option-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaAttributeOptionUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaAttributeOption: IMegaAttributeOption | null = null;

  megaAttributesSharedCollection = signal<IMegaAttribute[]>([]);

  protected megaAttributeOptionService = inject(MegaAttributeOptionService);
  protected megaAttributeOptionFormService = inject(MegaAttributeOptionFormService);
  protected megaAttributeService = inject(MegaAttributeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaAttributeOptionFormGroup = this.megaAttributeOptionFormService.createMegaAttributeOptionFormGroup();

  compareMegaAttribute = (o1: IMegaAttribute | null, o2: IMegaAttribute | null): boolean =>
    this.megaAttributeService.compareMegaAttribute(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaAttributeOption }) => {
      this.megaAttributeOption = megaAttributeOption;
      if (megaAttributeOption) {
        this.updateForm(megaAttributeOption);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaAttributeOption = this.megaAttributeOptionFormService.getMegaAttributeOption(this.editForm);
    if (megaAttributeOption.id === null) {
      this.subscribeToSaveResponse(this.megaAttributeOptionService.create(megaAttributeOption));
    } else {
      this.subscribeToSaveResponse(this.megaAttributeOptionService.update(megaAttributeOption));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaAttributeOption | null>): void {
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

  protected updateForm(megaAttributeOption: IMegaAttributeOption): void {
    this.megaAttributeOption = megaAttributeOption;
    this.megaAttributeOptionFormService.resetForm(this.editForm, megaAttributeOption);

    this.megaAttributesSharedCollection.update(megaAttributes =>
      this.megaAttributeService.addMegaAttributeToCollectionIfMissing<IMegaAttribute>(megaAttributes, megaAttributeOption.attribute),
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
            this.megaAttributeOption?.attribute,
          ),
        ),
      )
      .subscribe((megaAttributes: IMegaAttribute[]) => this.megaAttributesSharedCollection.set(megaAttributes));
  }
}
