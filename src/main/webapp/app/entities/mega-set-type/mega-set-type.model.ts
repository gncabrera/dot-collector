import { IMegaAttribute } from 'app/entities/mega-attribute/mega-attribute.model';

export interface IMegaSetType {
  id: number;
  name?: string | null;
  version?: number | null;
  active?: boolean | null;
  isLatest?: boolean | null;
  attributes?: Pick<IMegaAttribute, 'id'>[] | null;
}

export type NewMegaSetType = Omit<IMegaSetType, 'id'> & { id: null };
