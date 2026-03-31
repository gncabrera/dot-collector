import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { MegaSetService } from 'app/entities/mega-set/service/mega-set.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaSetPartCount } from '../mega-set-part-count.model';
import { MegaSetPartCountService } from '../service/mega-set-part-count.service';

import { MegaSetPartCountFormGroup, MegaSetPartCountFormService } from './mega-set-part-count-form.service';

@Component({
  selector: 'jhi-mega-set-part-count-update',
  templateUrl: './mega-set-part-count-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaSetPartCountUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaSetPartCount: IMegaSetPartCount | null = null;

  megaSetsSharedCollection = signal<IMegaSet[]>([]);
  megaPartsSharedCollection = signal<IMegaPart[]>([]);

  protected megaSetPartCountService = inject(MegaSetPartCountService);
  protected megaSetPartCountFormService = inject(MegaSetPartCountFormService);
  protected megaSetService = inject(MegaSetService);
  protected megaPartService = inject(MegaPartService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaSetPartCountFormGroup = this.megaSetPartCountFormService.createMegaSetPartCountFormGroup();

  compareMegaSet = (o1: IMegaSet | null, o2: IMegaSet | null): boolean => this.megaSetService.compareMegaSet(o1, o2);

  compareMegaPart = (o1: IMegaPart | null, o2: IMegaPart | null): boolean => this.megaPartService.compareMegaPart(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaSetPartCount }) => {
      this.megaSetPartCount = megaSetPartCount;
      if (megaSetPartCount) {
        this.updateForm(megaSetPartCount);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaSetPartCount = this.megaSetPartCountFormService.getMegaSetPartCount(this.editForm);
    if (megaSetPartCount.id === null) {
      this.subscribeToSaveResponse(this.megaSetPartCountService.create(megaSetPartCount));
    } else {
      this.subscribeToSaveResponse(this.megaSetPartCountService.update(megaSetPartCount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaSetPartCount | null>): void {
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

  protected updateForm(megaSetPartCount: IMegaSetPartCount): void {
    this.megaSetPartCount = megaSetPartCount;
    this.megaSetPartCountFormService.resetForm(this.editForm, megaSetPartCount);

    this.megaSetsSharedCollection.update(megaSets =>
      this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, megaSetPartCount.set),
    );
    this.megaPartsSharedCollection.update(megaParts =>
      this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, megaSetPartCount.part),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaSetService
      .query()
      .pipe(map((res: HttpResponse<IMegaSet[]>) => res.body ?? []))
      .pipe(
        map((megaSets: IMegaSet[]) => this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, this.megaSetPartCount?.set)),
      )
      .subscribe((megaSets: IMegaSet[]) => this.megaSetsSharedCollection.set(megaSets));

    this.megaPartService
      .query()
      .pipe(map((res: HttpResponse<IMegaPart[]>) => res.body ?? []))
      .pipe(
        map((megaParts: IMegaPart[]) =>
          this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, this.megaSetPartCount?.part),
        ),
      )
      .subscribe((megaParts: IMegaPart[]) => this.megaPartsSharedCollection.set(megaParts));
  }
}
