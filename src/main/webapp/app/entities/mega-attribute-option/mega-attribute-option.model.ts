import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';

export interface IMegaAttributeOption {
  id: number;
  label?: string | null;
  value?: string | null;
  description?: string | null;
  attribute?: Pick<IMegaAttribute, 'id'> | null;
}

export type NewMegaAttributeOption = Omit<IMegaAttributeOption, 'id'> & { id: null };
