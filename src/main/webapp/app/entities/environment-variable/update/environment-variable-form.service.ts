import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IEnvironmentVariable, NewEnvironmentVariable } from '../environment-variable.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEnvironmentVariable for edit and NewEnvironmentVariableFormGroupInput for create.
 */
type EnvironmentVariableFormGroupInput = IEnvironmentVariable | PartialWithRequiredKeyOf<NewEnvironmentVariable>;

type EnvironmentVariableFormDefaults = Pick<NewEnvironmentVariable, 'id'>;

type EnvironmentVariableFormGroupContent = {
  id: FormControl<IEnvironmentVariable['id'] | NewEnvironmentVariable['id']>;
  key: FormControl<IEnvironmentVariable['key']>;
  value: FormControl<IEnvironmentVariable['value']>;
  description: FormControl<IEnvironmentVariable['description']>;
  type: FormControl<IEnvironmentVariable['type']>;
};

export type EnvironmentVariableFormGroup = FormGroup<EnvironmentVariableFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EnvironmentVariableFormService {
  createEnvironmentVariableFormGroup(environmentVariable?: EnvironmentVariableFormGroupInput): EnvironmentVariableFormGroup {
    const environmentVariableRawValue = {
      ...this.getFormDefaults(),
      ...(environmentVariable ?? { id: null }),
    };
    return new FormGroup<EnvironmentVariableFormGroupContent>({
      id: new FormControl(
        { value: environmentVariableRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      key: new FormControl(environmentVariableRawValue.key),
      value: new FormControl(environmentVariableRawValue.value),
      description: new FormControl(environmentVariableRawValue.description),
      type: new FormControl(environmentVariableRawValue.type),
    });
  }

  getEnvironmentVariable(form: EnvironmentVariableFormGroup): IEnvironmentVariable | NewEnvironmentVariable {
    return form.getRawValue() as IEnvironmentVariable | NewEnvironmentVariable;
  }

  resetForm(form: EnvironmentVariableFormGroup, environmentVariable: EnvironmentVariableFormGroupInput): void {
    const environmentVariableRawValue = { ...this.getFormDefaults(), ...environmentVariable };
    form.reset({
      ...environmentVariableRawValue,
      id: { value: environmentVariableRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): EnvironmentVariableFormDefaults {
    return {
      id: null,
    };
  }
}
