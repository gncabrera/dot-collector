import { AttributeType } from 'app/entities/enumerations/attribute-type.model';
import { UIComponent } from 'app/entities/enumerations/ui-component.model';
import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { IMegaSetType } from 'app/entities/mega-set-type/mega-set-type.model';

export interface IMegaAttribute {
  id: number;
  name?: string | null;
  label?: string | null;
  description?: string | null;
  uiComponent?: keyof typeof UIComponent | null;
  type?: keyof typeof AttributeType | null;
  required?: boolean | null;
  multiple?: boolean | null;
  defaultValue?: string | null;
  minNumber?: number | null;
  maxNumber?: number | null;
  minLength?: number | null;
  maxLength?: number | null;
  regex?: string | null;
  order?: number | null;
  attributeGroup?: string | null;
  active?: boolean | null;
  setTypes?: Pick<IMegaSetType, 'id'>[] | null;
  partTypes?: Pick<IMegaPartType, 'id'>[] | null;
}

export type NewMegaAttribute = Omit<IMegaAttribute, 'id'> & { id: null };
