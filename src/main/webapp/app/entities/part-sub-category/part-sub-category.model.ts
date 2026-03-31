import { IMegaPart } from 'app/entities/mega-part/mega-part.model';

export interface IPartSubCategory {
  id: number;
  name?: string | null;
  description?: string | null;
  megaParts?: Pick<IMegaPart, 'id'>[] | null;
}

export type NewPartSubCategory = Omit<IPartSubCategory, 'id'> & { id: null };
