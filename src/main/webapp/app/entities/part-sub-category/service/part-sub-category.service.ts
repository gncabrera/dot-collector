import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPartSubCategory, NewPartSubCategory } from '../part-sub-category.model';

export type PartialUpdatePartSubCategory = Partial<IPartSubCategory> & Pick<IPartSubCategory, 'id'>;

@Injectable()
export class PartSubCategoriesService {
  readonly partSubCategoriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly partSubCategoriesResource = httpResource<IPartSubCategory[]>(() => {
    const params = this.partSubCategoriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of partSubCategory that have been fetched. It is updated when the partSubCategoriesResource emits a new value.
   * In case of error while fetching the partSubCategories, the signal is set to an empty array.
   */
  readonly partSubCategories = computed(() => (this.partSubCategoriesResource.hasValue() ? this.partSubCategoriesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/part-sub-categories');
}

@Injectable({ providedIn: 'root' })
export class PartSubCategoryService extends PartSubCategoriesService {
  protected readonly http = inject(HttpClient);

  create(partSubCategory: NewPartSubCategory): Observable<IPartSubCategory> {
    return this.http.post<IPartSubCategory>(this.resourceUrl, partSubCategory);
  }

  update(partSubCategory: IPartSubCategory): Observable<IPartSubCategory> {
    return this.http.put<IPartSubCategory>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPartSubCategoryIdentifier(partSubCategory))}`,
      partSubCategory,
    );
  }

  partialUpdate(partSubCategory: PartialUpdatePartSubCategory): Observable<IPartSubCategory> {
    return this.http.patch<IPartSubCategory>(
      `${this.resourceUrl}/${encodeURIComponent(this.getPartSubCategoryIdentifier(partSubCategory))}`,
      partSubCategory,
    );
  }

  find(id: number): Observable<IPartSubCategory> {
    return this.http.get<IPartSubCategory>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IPartSubCategory[]>> {
    const options = createRequestOption(req);
    return this.http.get<IPartSubCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPartSubCategoryIdentifier(partSubCategory: Pick<IPartSubCategory, 'id'>): number {
    return partSubCategory.id;
  }

  comparePartSubCategory(o1: Pick<IPartSubCategory, 'id'> | null, o2: Pick<IPartSubCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getPartSubCategoryIdentifier(o1) === this.getPartSubCategoryIdentifier(o2) : o1 === o2;
  }

  addPartSubCategoryToCollectionIfMissing<Type extends Pick<IPartSubCategory, 'id'>>(
    partSubCategoryCollection: Type[],
    ...partSubCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const partSubCategories: Type[] = partSubCategoriesToCheck.filter(isPresent);
    if (partSubCategories.length > 0) {
      const partSubCategoryCollectionIdentifiers = partSubCategoryCollection.map(partSubCategoryItem =>
        this.getPartSubCategoryIdentifier(partSubCategoryItem),
      );
      const partSubCategoriesToAdd = partSubCategories.filter(partSubCategoryItem => {
        const partSubCategoryIdentifier = this.getPartSubCategoryIdentifier(partSubCategoryItem);
        if (partSubCategoryCollectionIdentifiers.includes(partSubCategoryIdentifier)) {
          return false;
        }
        partSubCategoryCollectionIdentifiers.push(partSubCategoryIdentifier);
        return true;
      });
      return [...partSubCategoriesToAdd, ...partSubCategoryCollection];
    }
    return partSubCategoryCollection;
  }
}
