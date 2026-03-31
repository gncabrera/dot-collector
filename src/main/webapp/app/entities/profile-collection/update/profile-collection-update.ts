import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileCollection } from '../profile-collection.model';
import { ProfileCollectionService } from '../service/profile-collection.service';

import { ProfileCollectionFormGroup, ProfileCollectionFormService } from './profile-collection-form.service';

@Component({
  selector: 'jhi-profile-collection-update',
  templateUrl: './profile-collection-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfileCollectionUpdate implements OnInit {
  readonly isSaving = signal(false);
  profileCollection: IProfileCollection | null = null;

  profilesSharedCollection = signal<IProfile[]>([]);

  protected profileCollectionService = inject(ProfileCollectionService);
  protected profileCollectionFormService = inject(ProfileCollectionFormService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfileCollectionFormGroup = this.profileCollectionFormService.createProfileCollectionFormGroup();

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profileCollection }) => {
      this.profileCollection = profileCollection;
      if (profileCollection) {
        this.updateForm(profileCollection);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const profileCollection = this.profileCollectionFormService.getProfileCollection(this.editForm);
    if (profileCollection.id === null) {
      this.subscribeToSaveResponse(this.profileCollectionService.create(profileCollection));
    } else {
      this.subscribeToSaveResponse(this.profileCollectionService.update(profileCollection));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfileCollection | null>): void {
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

  protected updateForm(profileCollection: IProfileCollection): void {
    this.profileCollection = profileCollection;
    this.profileCollectionFormService.resetForm(this.editForm, profileCollection);

    this.profilesSharedCollection.update(profiles =>
      this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, profileCollection.profile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, this.profileCollection?.profile),
        ),
      )
      .subscribe((profiles: IProfile[]) => this.profilesSharedCollection.set(profiles));
  }
}
