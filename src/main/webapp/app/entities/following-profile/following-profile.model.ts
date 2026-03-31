import dayjs from 'dayjs/esm';

import { IProfile } from 'app/entities/profile/profile.model';

export interface IFollowingProfile {
  id: number;
  dateFollowing?: dayjs.Dayjs | null;
  profile?: Pick<IProfile, 'id'> | null;
  followedProfile?: Pick<IProfile, 'id'> | null;
}

export type NewFollowingProfile = Omit<IFollowingProfile, 'id'> & { id: null };
