import { IProfile } from 'app/entities/profile/profile.model';

export interface IProfileCollection {
  id: number;
  title?: string | null;
  description?: string | null;
  isPublic?: boolean | null;
  profile?: Pick<IProfile, 'id'> | null;
}

export type NewProfileCollection = Omit<IProfileCollection, 'id'> & { id: null };
