import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProfile } from 'app/entities/profile/profile.model';
import { ProfileService } from 'app/entities/profile/service/profile.service';
import { IProfileRequestType } from 'app/entities/profile-request-type/profile-request-type.model';
import { ProfileRequestTypeService } from 'app/entities/profile-request-type/service/profile-request-type.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequest } from '../profile-request.model';
import { ProfileRequestService } from '../service/profile-request.service';

import { ProfileRequestFormGroup, ProfileRequestFormService } from './profile-request-form.service';

@Component({
  selector: 'jhi-profile-request-update',
  templateUrl: './profile-request-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfileRequestUpdate implements OnInit {
  readonly isSaving = signal(false);
  profileRequest: IProfileRequest | null = null;

  profileRequestTypesSharedCollection = signal<IProfileRequestType[]>([]);
  profilesSharedCollection = signal<IProfile[]>([]);

  protected profileRequestService = inject(ProfileRequestService);
  protected profileRequestFormService = inject(ProfileRequestFormService);
  protected profileRequestTypeService = inject(ProfileRequestTypeService);
  protected profileService = inject(ProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfileRequestFormGroup = this.profileRequestFormService.createProfileRequestFormGroup();

  compareProfileRequestType = (o1: IProfileRequestType | null, o2: IProfileRequestType | null): boolean =>
    this.profileRequestTypeService.compareProfileRequestType(o1, o2);

  compareProfile = (o1: IProfile | null, o2: IProfile | null): boolean => this.profileService.compareProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profileRequest }) => {
      this.profileRequest = profileRequest;
      if (profileRequest) {
        this.updateForm(profileRequest);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const profileRequest = this.profileRequestFormService.getProfileRequest(this.editForm);
    if (profileRequest.id === null) {
      this.subscribeToSaveResponse(this.profileRequestService.create(profileRequest));
    } else {
      this.subscribeToSaveResponse(this.profileRequestService.update(profileRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfileRequest | null>): void {
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

  protected updateForm(profileRequest: IProfileRequest): void {
    this.profileRequest = profileRequest;
    this.profileRequestFormService.resetForm(this.editForm, profileRequest);

    this.profileRequestTypesSharedCollection.update(profileRequestTypes =>
      this.profileRequestTypeService.addProfileRequestTypeToCollectionIfMissing<IProfileRequestType>(
        profileRequestTypes,
        profileRequest.type,
      ),
    );
    this.profilesSharedCollection.update(profiles =>
      this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, profileRequest.profile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.profileRequestTypeService
      .query()
      .pipe(map((res: HttpResponse<IProfileRequestType[]>) => res.body ?? []))
      .pipe(
        map((profileRequestTypes: IProfileRequestType[]) =>
          this.profileRequestTypeService.addProfileRequestTypeToCollectionIfMissing<IProfileRequestType>(
            profileRequestTypes,
            this.profileRequest?.type,
          ),
        ),
      )
      .subscribe((profileRequestTypes: IProfileRequestType[]) => this.profileRequestTypesSharedCollection.set(profileRequestTypes));

    this.profileService
      .query()
      .pipe(map((res: HttpResponse<IProfile[]>) => res.body ?? []))
      .pipe(
        map((profiles: IProfile[]) =>
          this.profileService.addProfileToCollectionIfMissing<IProfile>(profiles, this.profileRequest?.profile),
        ),
      )
      .subscribe((profiles: IProfile[]) => this.profilesSharedCollection.set(profiles));
  }
}
