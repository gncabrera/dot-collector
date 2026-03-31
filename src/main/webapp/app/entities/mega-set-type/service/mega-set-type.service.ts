import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaSetType, NewMegaSetType } from '../mega-set-type.model';

export type PartialUpdateMegaSetType = Partial<IMegaSetType> & Pick<IMegaSetType, 'id'>;

@Injectable()
export class MegaSetTypesService {
  readonly megaSetTypesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaSetTypesResource = httpResource<IMegaSetType[]>(() => {
    const params = this.megaSetTypesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaSetType that have been fetched. It is updated when the megaSetTypesResource emits a new value.
   * In case of error while fetching the megaSetTypes, the signal is set to an empty array.
   */
  readonly megaSetTypes = computed(() => (this.megaSetTypesResource.hasValue() ? this.megaSetTypesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-set-types');
}

@Injectable({ providedIn: 'root' })
export class MegaSetTypeService extends MegaSetTypesService {
  protected readonly http = inject(HttpClient);

  create(megaSetType: NewMegaSetType): Observable<IMegaSetType> {
    return this.http.post<IMegaSetType>(this.resourceUrl, megaSetType);
  }

  update(megaSetType: IMegaSetType): Observable<IMegaSetType> {
    return this.http.put<IMegaSetType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaSetTypeIdentifier(megaSetType))}`,
      megaSetType,
    );
  }

  partialUpdate(megaSetType: PartialUpdateMegaSetType): Observable<IMegaSetType> {
    return this.http.patch<IMegaSetType>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaSetTypeIdentifier(megaSetType))}`,
      megaSetType,
    );
  }

  find(id: number): Observable<IMegaSetType> {
    return this.http.get<IMegaSetType>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaSetType[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaSetType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaSetTypeIdentifier(megaSetType: Pick<IMegaSetType, 'id'>): number {
    return megaSetType.id;
  }

  compareMegaSetType(o1: Pick<IMegaSetType, 'id'> | null, o2: Pick<IMegaSetType, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaSetTypeIdentifier(o1) === this.getMegaSetTypeIdentifier(o2) : o1 === o2;
  }

  addMegaSetTypeToCollectionIfMissing<Type extends Pick<IMegaSetType, 'id'>>(
    megaSetTypeCollection: Type[],
    ...megaSetTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaSetTypes: Type[] = megaSetTypesToCheck.filter(isPresent);
    if (megaSetTypes.length > 0) {
      const megaSetTypeCollectionIdentifiers = megaSetTypeCollection.map(megaSetTypeItem => this.getMegaSetTypeIdentifier(megaSetTypeItem));
      const megaSetTypesToAdd = megaSetTypes.filter(megaSetTypeItem => {
        const megaSetTypeIdentifier = this.getMegaSetTypeIdentifier(megaSetTypeItem);
        if (megaSetTypeCollectionIdentifiers.includes(megaSetTypeIdentifier)) {
          return false;
        }
        megaSetTypeCollectionIdentifiers.push(megaSetTypeIdentifier);
        return true;
      });
      return [...megaSetTypesToAdd, ...megaSetTypeCollection];
    }
    return megaSetTypeCollection;
  }
}
