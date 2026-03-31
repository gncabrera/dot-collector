import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPartCategory, NewPartCategory } from '../part-category.model';

export type PartialUpdatePartCategory = Partial<IPartCategory> & Pick<IPartCategory, 'id'>;

@Injectable()
export class PartCategoriesService {
  readonly partCategoriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly partCategoriesResource = httpResource<IPartCategory[]>(() => {
    const params = this.partCategoriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of partCategory that have been fetched. It is updated when the partCategoriesResource emits a new value.
   * In case of error while fetching the partCategories, the signal is set to an empty array.
   */
  readonly partCategories = computed(() => (this.partCategoriesResource.hasValue() ? this.partCategoriesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/part-categories');
}

@Injectable({ providedIn: 'root' })
export class PartCategoryService extends PartCategoriesService {
  protected readonly http = inject(HttpClient);

  create(partCategory: NewPartCategory): Observable<IPartCategory> {
    return this.http.post<IPartCategory>(this.resourceUrl, partCategory);
  }

  update(partCategory: IPartCategory): Observable<IPartCategory> {
    return this.http.put<IPartCategory>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPartCategoryIdentifier(partCategory))}`,
      partCategory,
    );
  }

  partialUpdate(partCategory: PartialUpdatePartCategory): Observable<IPartCategory> {
    return this.http.patch<IPartCategory>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPartCategoryIdentifier(partCategory))}`,
      partCategory,
    );
  }

  find(id: number): Observable<IPartCategory> {
    return this.http.get<IPartCategory>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPartCategory[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPartCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPartCategoryIdentifier(partCategory: Pick<IPartCategory, 'id'>): number {
    return partCategory.id;
  }

  comparePartCategory(o1: Pick<IPartCategory, 'id'> | null, o2: Pick<IPartCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartCategoryIdentifier(o1) === this.getPartCategoryIdentifier(o2) : o1 === o2;
  }

  addPartCategoryToCollectionIfMissing<Type extends Pick<IPartCategory, 'id'>>(
    partCategoryCollection: Type[],
    ...partCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const partCategories: Type[] = partCategoriesToCheck.filter(isPresent);
    if (partCategories.length > 0) {
      const partCategoryCollectionIdentifiers = partCategoryCollection.map(partCategoryItem =>
        this.getPartCategoryIdentifier(partCategoryItem),
      );
      const partCategoriesToAdd = partCategories.filter(partCategoryItem => {
        const partCategoryIdentifier = this.getPartCategoryIdentifier(partCategoryItem);
        if (partCategoryCollectionIdentifiers.includes(partCategoryIdentifier)) {
          return false;
        }
        partCategoryCollectionIdentifiers.push(partCategoryIdentifier);
        return true;
      });
      return [...partCategoriesToAdd, ...partCategoryCollection];
    }
    return partCategoryCollection;
  }
}
