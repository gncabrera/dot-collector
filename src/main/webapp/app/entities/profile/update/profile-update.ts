import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { IProfile } from '../profile.model';
import { ProfileService } from '../service/profile.service';

import { ProfileFormGroup, ProfileFormService } from './profile-form.service';

@Component({
  selector: 'jhi-profile-update',
  templateUrl: './profile-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  profile: IProfile | null = null;

  usersSharedCollection = signal<IUser[]>([]);

  protected profileService = inject(ProfileService);
  protected profileFormService = inject(ProfileFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProfileFormGroup = this.profileFormService.createProfileFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ profile }) => {
      this.profile = profile;
      if (profile) {
        this.updateForm(profile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const profile = this.profileFormService.getProfile(this.editForm);
    if (profile.id === null) {
      this.subscribeToSaveResponse(this.profileService.create(profile));
    } else {
      this.subscribeToSaveResponse(this.profileService.update(profile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProfile | null>): void {
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

  protected updateForm(profile: IProfile): void {
    this.profile = profile;
    this.profileFormService.resetForm(this.editForm, profile);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, profile.user));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.profile?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));
  }
}
