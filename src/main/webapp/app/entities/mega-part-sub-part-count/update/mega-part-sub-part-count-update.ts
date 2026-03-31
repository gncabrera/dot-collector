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
import { IMegaPartSubPartCount } from '../mega-part-sub-part-count.model';
import { MegaPartSubPartCountService } from '../service/mega-part-sub-part-count.service';

import { MegaPartSubPartCountFormGroup, MegaPartSubPartCountFormService } from './mega-part-sub-part-count-form.service';

@Component({
  selector: 'jhi-mega-part-sub-part-count-update',
  templateUrl: './mega-part-sub-part-count-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaPartSubPartCountUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaPartSubPartCount: IMegaPartSubPartCount | null = null;

  megaPartsSharedCollection = signal<IMegaPart[]>([]);

  protected megaPartSubPartCountService = inject(MegaPartSubPartCountService);
  protected megaPartSubPartCountFormService = inject(MegaPartSubPartCountFormService);
  protected megaPartService = inject(MegaPartService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaPartSubPartCountFormGroup = this.megaPartSubPartCountFormService.createMegaPartSubPartCountFormGroup();

  compareMegaPart = (o1: IMegaPart | null, o2: IMegaPart | null): boolean => this.megaPartService.compareMegaPart(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaPartSubPartCount }) => {
      this.megaPartSubPartCount = megaPartSubPartCount;
      if (megaPartSubPartCount) {
        this.updateForm(megaPartSubPartCount);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaPartSubPartCount = this.megaPartSubPartCountFormService.getMegaPartSubPartCount(this.editForm);
    if (megaPartSubPartCount.id === null) {
      this.subscribeToSaveResponse(this.megaPartSubPartCountService.create(megaPartSubPartCount));
    } else {
      this.subscribeToSaveResponse(this.megaPartSubPartCountService.update(megaPartSubPartCount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaPartSubPartCount | null>): void {
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

  protected updateForm(megaPartSubPartCount: IMegaPartSubPartCount): void {
    this.megaPartSubPartCount = megaPartSubPartCount;
    this.megaPartSubPartCountFormService.resetForm(this.editForm, megaPartSubPartCount);

    this.megaPartsSharedCollection.update(megaParts =>
      this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(
        megaParts,
        megaPartSubPartCount.part,
        megaPartSubPartCount.parentPart,
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaPartService
      .query()
      .pipe(map((res: HttpResponse<IMegaPart[]>) => res.body ?? []))
      .pipe(
        map((megaParts: IMegaPart[]) =>
          this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(
            megaParts,
            this.megaPartSubPartCount?.part,
            this.megaPartSubPartCount?.parentPart,
          ),
        ),
      )
      .subscribe((megaParts: IMegaPart[]) => this.megaPartsSharedCollection.set(megaParts));
  }
}
