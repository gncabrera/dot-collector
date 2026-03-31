import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IFollowingProfile } from '../following-profile.model';
import { FollowingProfileService } from '../service/following-profile.service';

import { FollowingProfileFormGroup, FollowingProfileFormService } from './following-profile-form.service';

@Component({
  selector: 'jhi-following-profile-update',
  templateUrl: './following-profile-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class FollowingProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  followingProfile: IFollowingProfile | null = null;

  profilesSharedCollection = signal<IProfile[]>([]);

  protected followingProfileService = inject(FollowingProfileService);
  protected followingProfileFormService = inject(FollowingProfileFormService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: FollowingProfileFormGroup = this.followingProfileFormService.createFollowingProfileFormGroup();

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ followingProfile }) => {
      this.followingProfile = followingProfile;
      if (followingProfile) {
        this.updateForm(followingProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const followingProfile = this.followingProfileFormService.getFollowingProfile(this.editForm);
    if (followingProfile.id === null) {
      this.subscribeToSaveResponse(this.followingProfileService.create(followingProfile));
    } else {
      this.subscribeToSaveResponse(this.followingProfileService.update(followingProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IFollowingProfile | null>): void {
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

  protected updateForm(followingProfile: IFollowingProfile): void {
    this.followingProfile = followingProfile;
    this.followingProfileFormService.resetForm(this.editForm, followingProfile);

    this.profilesSharedCollection.update(profiles =>
      this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, followingProfile.profile, followingProfile.followedProfile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing<IProfile>(
            profiles,
            this.followingProfile?.profile,
            this.followingProfile?.followedProfile,
          ),
        ),
      )
      .subscribe((profiles: IProfile[]) => this.profilesSharedCollection.set(profiles));
  }
}
