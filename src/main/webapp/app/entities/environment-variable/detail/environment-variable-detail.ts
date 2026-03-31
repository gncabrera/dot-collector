import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { IEnvironmentVariable } from '../environment-variable.model';

@Component({
  selector: 'jhi-environment-variable-detail',
  templateUrl: './environment-variable-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, RouterLink],
})
export class EnvironmentVariableDetail {
  readonly environmentVariable = input<IEnvironmentVariable | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
