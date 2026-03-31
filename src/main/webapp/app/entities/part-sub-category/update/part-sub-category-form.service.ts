import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPartSubCategory, NewPartSubCategory } from '../part-sub-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPartSubCategory for edit and NewPartSubCategoryFormGroupInput for create.
 */
type PartSubCategoryFormGroupInput = IPartSubCategory | PartialWithRequiredKeyOf<NewPartSubCategory>;

type PartSubCategoryFormDefaults = Pick<NewPartSubCategory, 'id' | 'megaParts'>;

type PartSubCategoryFormGroupContent = {
  id: FormControl<IPartSubCategory['id'] | NewPartSubCategory['id']>;
  name: FormControl<IPartSubCategory['name']>;
  description: FormControl<IPartSubCategory['description']>;
  megaParts: FormControl<IPartSubCategory['megaParts']>;
};

export type PartSubCategoryFormGroup = FormGroup<PartSubCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartSubCategoryFormService {
  createPartSubCategoryFormGroup(partSubCategory?: PartSubCategoryFormGroupInput): PartSubCategoryFormGroup {
    const partSubCategoryRawValue = {
      ...this.getFormDefaults(),
      ...(partSubCategory ?? { id: null }),
    };
    return new FormGroup<PartSubCategoryFormGroupContent>({
      id: new FormControl(
        { value: partSubCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(partSubCategoryRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(partSubCategoryRawValue.description),
      megaParts: new FormControl(partSubCategoryRawValue.megaParts ?? []),
    });
  }

  getPartSubCategory(form: PartSubCategoryFormGroup): IPartSubCategory | NewPartSubCategory {
    return form.getRawValue() as IPartSubCategory | NewPartSubCategory;
  }

  resetForm(form: PartSubCategoryFormGroup, partSubCategory: PartSubCategoryFormGroupInput): void {
    const partSubCategoryRawValue = { ...this.getFormDefaults(), ...partSubCategory };
    form.reset({
      ...partSubCategoryRawValue,
      id: { value: partSubCategoryRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PartSubCategoryFormDefaults {
    return {
      id: null,
      megaParts: [],
    };
  }
}
