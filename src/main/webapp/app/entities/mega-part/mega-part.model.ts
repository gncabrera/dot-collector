import dayjs from 'dayjs/esm';

import { IMegaPartType } from 'app/entities/mega-part-type/mega-part-type.model';
import { IPartCategory } from 'app/entities/part-category/part-category.model';
import { IPartSubCategory } from 'app/entities/part-sub-category/part-sub-category.model';

export interface IMegaPart {
  id: number;
  releaseDate?: dayjs.Dayjs | null;
  partNumber?: string | null;
  nameEN?: string | null;
  nameES?: string | null;
  nameDE?: string | null;
  nameFR?: string | null;
  description?: string | null;
  notes?: string | null;
  attributes?: string | null;
  attributesContentType?: string | null;
  type?: Pick<IMegaPartType, 'id'> | null;
  partCategory?: Pick<IPartCategory, 'id'> | null;
  partSubCategories?: Pick<IPartSubCategory, 'id'>[] | null;
}

export type NewMegaPart = Omit<IMegaPart, 'id'> & { id: null };
