import { IUser } from 'app/entities/user/user.model';

export interface IProfile {
  id: number;
  username?: string | null;
  fullName?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewProfile = Omit<IProfile, 'id'> & { id: null };
