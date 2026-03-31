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
import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';
import { MegaSetTypeService } from 'app/entities/mega-set-type/service/mega-set-type.service';
import { IProfileCollectionSet } from 'app/entities/profile-collection-set/profile-collection-set.model';
import { ProfileCollectionSetService } from 'app/entities/profile-collection-set/service/profile-collection-set.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { IMegaSet } from '../mega-set.model';
import { MegaSetService } from '../service/mega-set.service';

import { MegaSetFormGroup, MegaSetFormService } from './mega-set-form.service';

@Component({
  selector: 'jhi-mega-set-update',
  templateUrl: './mega-set-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class MegaSetUpdate implements OnInit {
  readonly isSaving = signal(false);
  megaSet: IMegaSet | null = null;

  megaSetTypesSharedCollection = signal<IMegaSetType[]>([]);
  profileCollectionSetsSharedCollection = signal<IProfileCollectionSet[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected megaSetService = inject(MegaSetService);
  protected megaSetFormService = inject(MegaSetFormService);
  protected megaSetTypeService = inject(MegaSetTypeService);
  protected profileCollectionSetService = inject(ProfileCollectionSetService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: MegaSetFormGroup = this.megaSetFormService.createMegaSetFormGroup();

  compareMegaSetType = (o1: IMegaSetType | null, o2: IMegaSetType | null): boolean => this.megaSetTypeService.compareMegaSetType(o1, o2);

  compareProfileCollectionSet = (o1: IProfileCollectionSet | null, o2: IProfileCollectionSet | null): boolean =>
    this.profileCollectionSetService.compareProfileCollectionSet(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ megaSet }) => {
      this.megaSet = megaSet;
      if (megaSet) {
        this.updateForm(megaSet);
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
    const megaSet = this.megaSetFormService.getMegaSet(this.editForm);
    if (megaSet.id === null) {
      this.subscribeToSaveResponse(this.megaSetService.create(megaSet));
    } else {
      this.subscribeToSaveResponse(this.megaSetService.update(megaSet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IMegaSet | null>): void {
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

  protected updateForm(megaSet: IMegaSet): void {
    this.megaSet = megaSet;
    this.megaSetFormService.resetForm(this.editForm, megaSet);

    this.megaSetTypesSharedCollection.update(megaSetTypes =>
      this.megaSetTypeService.addMegaSetTypeToCollectionIfMissing<IMegaSetType>(megaSetTypes, megaSet.type),
    );
    this.profileCollectionSetsSharedCollection.update(profileCollectionSets =>
      this.profileCollectionSetService.addProfileCollectionSetToCollectionIfMissing<IProfileCollectionSet>(
        profileCollectionSets,
        ...(megaSet.profileCollectionSets ?? []),
      ),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.megaSetTypeService
      .query()
      .pipe(map((res: HttpResponse<IMegaSetType[]>) => res.body ?? []))
      .pipe(
        map((megaSetTypes: IMegaSetType[]) =>
          this.megaSetTypeService.addMegaSetTypeToCollectionIfMissing<IMegaSetType>(megaSetTypes, this.megaSet?.type),
        ),
      )
      .subscribe((megaSetTypes: IMegaSetType[]) => this.megaSetTypesSharedCollection.set(megaSetTypes));

    this.profileCollectionSetService
      .query()
      .pipe(map((res: HttpResponse<IProfileCollectionSet[]>) => res.body ?? []))
      .pipe(
        map((profileCollectionSets: IProfileCollectionSet[]) =>
          this.profileCollectionSetService.addProfileCollectionSetToCollectionIfMissing<IProfileCollectionSet>(
            profileCollectionSets,
            ...(this.megaSet?.profileCollectionSets ?? []),
          ),
        ),
      )
      .subscribe((profileCollectionSets: IProfileCollectionSet[]) => this.profileCollectionSetsSharedCollection.set(profileCollectionSets));
  }
}
