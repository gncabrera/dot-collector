import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IProfileRequestType } from '../profile-request-type.model';
import { ProfileRequestTypeService } from '../service/profile-request-type.service';

import { ProfileRequestTypeFormGroup, ProfileRequestTypeFormService } from './profile-request-type-form.service';

@Component({
  selector: 'jhi-profile-request-type-update',
  templateUrl: './profile-request-type-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfileRequestTypeUpdate implements OnInit {
  readonly isSaving = signal(false);
  profileRequestType: IProfileRequestType | null = null;

  protected profileRequestTypeService = inject(ProfileRequestTypeService);
  protected profileRequestTypeFormService = inject(ProfileRequestTypeFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfileRequestTypeFormGroup = this.profileRequestTypeFormService.createProfileRequestTypeFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profileRequestType }) => {
      this.profileRequestType = profileRequestType;
      if (profileRequestType) {
        this.updateForm(profileRequestType);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const profileRequestType = this.profileRequestTypeFormService.getProfileRequestType(this.editForm);
    if (profileRequestType.id === null) {
      this.subscribeToSaveResponse(this.profileRequestTypeService.create(profileRequestType));
    } else {
      this.subscribeToSaveResponse(this.profileRequestTypeService.update(profileRequestType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfileRequestType | null>): void {
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

  protected updateForm(profileRequestType: IProfileRequestType): void {
    this.profileRequestType = profileRequestType;
    this.profileRequestTypeFormService.resetForm(this.editForm, profileRequestType);
  }
}
