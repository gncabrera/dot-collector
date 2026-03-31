import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { AssetType } from 'app/entities/enumerations/asset-type.model';
import { IMegaPart } from 'app/entities/mega-part/mega-part.model';
import { MegaPartService } from 'app/entities/mega-part/service/mega-part.service';
import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { MegaSetService } from 'app/entities/mega-set/service/mega-set.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IMegaAsset } from '../mega-asset.model';
import { MegaAssetService } from '../service/mega-asset.service';

import { MegaAssetFormGroup, MegaAssetFormService } from './mega-asset-form.service';

@Component({
  selector: 'jhi-mega-asset-update',
  templateUrl: './mega-asset-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class MegaAssetUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaAsset: IMegaAsset | null = null;
  assetTypeValues = Object.keys(AssetType);

  megaSetsSharedCollection = signal<IMegaSet[]>([]);
  megaPartsSharedCollection = signal<IMegaPart[]>([]);

  protected megaAssetService = inject(MegaAssetService);
  protected megaAssetFormService = inject(MegaAssetFormService);
  protected megaSetService = inject(MegaSetService);
  protected megaPartService = inject(MegaPartService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaAssetFormGroup = this.megaAssetFormService.createMegaAssetFormGroup();

  compareMegaSet = (o1: IMegaSet | null, o2: IMegaSet | null): boolean => this.megaSetService.compareMegaSet(o1, o2);

  compareMegaPart = (o1: IMegaPart | null, o2: IMegaPart | null): boolean => this.megaPartService.compareMegaPart(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaAsset }) => {
      this.megaAsset = megaAsset;
      if (megaAsset) {
        this.updateForm(megaAsset);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const megaAsset = this.megaAssetFormService.getMegaAsset(this.editForm);
    if (megaAsset.id === null) {
      this.subscribeToSaveResponse(this.megaAssetService.create(megaAsset));
    } else {
      this.subscribeToSaveResponse(this.megaAssetService.update(megaAsset));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaAsset | null>): void {
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

  protected updateForm(megaAsset: IMegaAsset): void {
    this.megaAsset = megaAsset;
    this.megaAssetFormService.resetForm(this.editForm, megaAsset);

    this.megaSetsSharedCollection.update(megaSets =>
      this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, megaAsset.set),
    );
    this.megaPartsSharedCollection.update(megaParts =>
      this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, megaAsset.part),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaSetService
      .query()
      .pipe(map((res: HttpResponse<IMegaSet[]>) => res.body ?? []))
      .pipe(map((megaSets: IMegaSet[]) => this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, this.megaAsset?.set)))
      .subscribe((megaSets: IMegaSet[]) => this.megaSetsSharedCollection.set(megaSets));

    this.megaPartService
      .query()
      .pipe(map((res: HttpResponse<IMegaPart[]>) => res.body ?? []))
      .pipe(
        map((megaParts: IMegaPart[]) => this.megaPartService.addMegaPartToCollectionIfMissing<IMegaPart>(megaParts, this.megaAsset?.part)),
      )
      .subscribe((megaParts: IMegaPart[]) => this.megaPartsSharedCollection.set(megaParts));
  }
}
