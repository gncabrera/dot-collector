import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { IEnvironmentVariable } from '../environment-variable.model';
import { EnvironmentVariableService } from '../service/environment-variable.service';

import { EnvironmentVariableFormGroup, EnvironmentVariableFormService } from './environment-variable-form.service';

@Component({
  selector: 'jhi-environment-variable-update',
  templateUrl: './environment-variable-update.html',
  imports: [FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class EnvironmentVariableUpdate implements OnInit {
  readonly isSaving = signal(false);
  environmentVariable: IEnvironmentVariable | null = null;

  protected environmentVariableService = inject(EnvironmentVariableService);
  protected environmentVariableFormService = inject(EnvironmentVariableFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: EnvironmentVariableFormGroup = this.environmentVariableFormService.createEnvironmentVariableFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ environmentVariable }) => {
      this.environmentVariable = environmentVariable;
      if (environmentVariable) {
        this.updateForm(environmentVariable);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const environmentVariable = this.environmentVariableFormService.getEnvironmentVariable(this.editForm);
    if (environmentVariable.id === null) {
      this.subscribeToSaveResponse(this.environmentVariableService.create(environmentVariable));
    } else {
      this.subscribeToSaveResponse(this.environmentVariableService.update(environmentVariable));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IEnvironmentVariable | null>): void {
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

  protected updateForm(environmentVariable: IEnvironmentVariable): void {
    this.environmentVariable = environmentVariable;
    this.environmentVariableFormService.resetForm(this.editForm, environmentVariable);
  }
}
