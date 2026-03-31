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
import { IBlockedProfile } from '../blocked-profile.model';
import { BlockedProfileService } from '../service/blocked-profile.service';

import { BlockedProfileFormGroup, BlockedProfileFormService } from './blocked-profile-form.service';

@Component({
  selector: 'jhi-blocked-profile-update',
  templateUrl: './blocked-profile-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule, NgbInputDatepicker],
})
export class BlockedProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  blockedProfile: IBlockedProfile | null = null;

  profilesSharedCollection = signal<IProfile[]>([]);

  protected blockedProfileService = inject(BlockedProfileService);
  protected blockedProfileFormService = inject(BlockedProfileFormService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: BlockedProfileFormGroup = this.blockedProfileFormService.createBlockedProfileFormGroup();

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ blockedProfile }) => {
      this.blockedProfile = blockedProfile;
      if (blockedProfile) {
        this.updateForm(blockedProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const blockedProfile = this.blockedProfileFormService.getBlockedProfile(this.editForm);
    if (blockedProfile.id === null) {
      this.subscribeToSaveResponse(this.blockedProfileService.create(blockedProfile));
    } else {
      this.subscribeToSaveResponse(this.blockedProfileService.update(blockedProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IBlockedProfile | null>): void {
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

  protected updateForm(blockedProfile: IBlockedProfile): void {
    this.blockedProfile = blockedProfile;
    this.blockedProfileFormService.resetForm(this.editForm, blockedProfile);

    this.profilesSharedCollection.update(profiles =>
      this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, blockedProfile.profile, blockedProfile.blockedProfile),
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
            this.blockedProfile?.profile,
            this.blockedProfile?.blockedProfile,
          ),
        ),
      )
      .subscribe((profiles: IProfile[]) => this.profilesSharedCollection.set(profiles));
  }
}
