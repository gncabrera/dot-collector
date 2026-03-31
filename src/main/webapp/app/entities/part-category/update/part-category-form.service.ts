import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPartCategory, NewPartCategory } from '../part-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPartCategory for edit and NewPartCategoryFormGroupInput for create.
 */
type PartCategoryFormGroupInput = IPartCategory | PartialWithRequiredKeyOf<NewPartCategory>;

type PartCategoryFormDefaults = Pick<NewPartCategory, 'id'>;

type PartCategoryFormGroupContent = {
  id: FormControl<IPartCategory['id'] | NewPartCategory['id']>;
  name: FormControl<IPartCategory['name']>;
  description: FormControl<IPartCategory['description']>;
};

export type PartCategoryFormGroup = FormGroup<PartCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartCategoryFormService {
  createPartCategoryFormGroup(partCategory?: PartCategoryFormGroupInput): PartCategoryFormGroup {
    const partCategoryRawValue = {
      ...this.getFormDefaults(),
      ...(partCategory ?? { id: null }),
    };
    return new FormGroup<PartCategoryFormGroupContent>({
      id: new FormControl(
        { value: partCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(partCategoryRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(partCategoryRawValue.description),
    });
  }

  getPartCategory(form: PartCategoryFormGroup): IPartCategory | NewPartCategory {
    return form.getRawValue() as IPartCategory | NewPartCategory;
  }

  resetForm(form: PartCategoryFormGroup, partCategory: PartCategoryFormGroupInput): void {
    const partCategoryRawValue = { ...this.getFormDefaults(), ...partCategory };
    form.reset({
      ...partCategoryRawValue,
      id: { value: partCategoryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PartCategoryFormDefaults {
    return {
      id: null,
    };
  }
}
