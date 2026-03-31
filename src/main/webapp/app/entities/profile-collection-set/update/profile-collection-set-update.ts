import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IMegaSet } from 'app/entities/mega-set/mega-set.model';
import { MegaSetService } from 'app/entities/mega-set/service/mega-set.service';
import { IProfileCollection } from 'app/entities/profile-collection/profile-collection.model';
import { ProfileCollectionService } from 'app/entities/profile-collection/service/profile-collection.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileCollectionSet } from '../profile-collection-set.model';
import { ProfileCollectionSetService } from '../service/profile-collection-set.service';

import { ProfileCollectionSetFormGroup, ProfileCollectionSetFormService } from './profile-collection-set-form.service';

@Component({
  selector: 'jhi-profile-collection-set-update',
  templateUrl: './profile-collection-set-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class ProfileCollectionSetUpdate implements OnInit {
  readonly isSaving = signal(false);
  profileCollectionSet: IProfileCollectionSet | null = null;

  profileCollectionsSharedCollection = signal<IProfileCollection[]>([]);
  megaSetsSharedCollection = signal<IMegaSet[]>([]);

  protected profileCollectionSetService = inject(ProfileCollectionSetService);
  protected profileCollectionSetFormService = inject(ProfileCollectionSetFormService);
  protected profileCollectionService = inject(ProfileCollectionService);
  protected megaSetService = inject(MegaSetService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfileCollectionSetFormGroup = this.profileCollectionSetFormService.createProfileCollectionSetFormGroup();

  compareProfileCollection = (o1: IProfileCollection | null, o2: IProfileCollection | null): boolean =>
    this.profileCollectionService.compareProfileCollection(o1, o2);

  compareMegaSet = (o1: IMegaSet | null, o2: IMegaSet | null): boolean => this.megaSetService.compareMegaSet(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profileCollectionSet }) => {
      this.profileCollectionSet = profileCollectionSet;
      if (profileCollectionSet) {
        this.updateForm(profileCollectionSet);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const profileCollectionSet = this.profileCollectionSetFormService.getProfileCollectionSet(this.editForm);
    if (profileCollectionSet.id === null) {
      this.subscribeToSaveResponse(this.profileCollectionSetService.create(profileCollectionSet));
    } else {
      this.subscribeToSaveResponse(this.profileCollectionSetService.update(profileCollectionSet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfileCollectionSet | null>): void {
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

  protected updateForm(profileCollectionSet: IProfileCollectionSet): void {
    this.profileCollectionSet = profileCollectionSet;
    this.profileCollectionSetFormService.resetForm(this.editForm, profileCollectionSet);

    this.profileCollectionsSharedCollection.update(profileCollections =>
      this.profileCollectionService.addProfileCollectionToCollectionIfMissing<IProfileCollection>(
        profileCollections,
        profileCollectionSet.collection,
      ),
    );
    this.megaSetsSharedCollection.update(megaSets =>
      this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, ...(profileCollectionSet.sets ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileCollectionService
      .query()
      .pipe(map((res: HttpResponse<IProfileCollection[]>) => res.body ?? []))
      .pipe(
        map((profileCollections: IProfileCollection[]) =>
          this.profileCollectionService.addProfileCollectionToCollectionIfMissing<IProfileCollection>(
            profileCollections,
            this.profileCollectionSet?.collection,
          ),
        ),
      )
      .subscribe((profileCollections: IProfileCollection[]) => this.profileCollectionsSharedCollection.set(profileCollections));

    this.megaSetService
      .query()
      .pipe(map((res: HttpResponse<IMegaSet[]>) => res.body ?? []))
      .pipe(
        map((megaSets: IMegaSet[]) =>
          this.megaSetService.addMegaSetToCollectionIfMissing<IMegaSet>(megaSets, ...(this.profileCollectionSet?.sets ?? [])),
        ),
      )
      .subscribe((megaSets: IMegaSet[]) => this.megaSetsSharedCollection.set(megaSets));
  }
}
