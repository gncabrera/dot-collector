export interface IPartCategory {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewPartCategory = Omit<IPartCategory, 'id'> & { id: null };
