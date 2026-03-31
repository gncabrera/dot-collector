import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IMegaAttribute, NewMegaAttribute } from '../mega-attribute.model';

export type PartialUpdateMegaAttribute = Partial<IMegaAttribute> & Pick<IMegaAttribute, 'id'>;

@Injectable()
export class MegaAttributesService {
  readonly megaAttributesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly megaAttributesResource = httpResource<IMegaAttribute[]>(() => {
    const params = this.megaAttributesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of megaAttribute that have been fetched. It is updated when the megaAttributesResource emits a new value.
   * In case of error while fetching the megaAttributes, the signal is set to an empty array.
   */
  readonly megaAttributes = computed(() => (this.megaAttributesResource.hasValue() ? this.megaAttributesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/mega-attributes');
}

@Injectable({ providedIn: 'root' })
export class MegaAttributeService extends MegaAttributesService {
  protected readonly http = inject(HttpClient);

  create(megaAttribute: NewMegaAttribute): Observable<IMegaAttribute> {
    return this.http.post<IMegaAttribute>(this.resourceUrl, megaAttribute);
  }

  update(megaAttribute: IMegaAttribute): Observable<IMegaAttribute> {
    return this.http.put<IMegaAttribute>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaAttributeIdentifier(megaAttribute))}`,
      megaAttribute,
    );
  }

  partialUpdate(megaAttribute: PartialUpdateMegaAttribute): Observable<IMegaAttribute> {
    return this.http.patch<IMegaAttribute>(
      `${this.resourceUrl}/${encodeURIComponent(this.getMegaAttributeIdentifier(megaAttribute))}`,
      megaAttribute,
    );
  }

  find(id: number): Observable<IMegaAttribute> {
    return this.http.get<IMegaAttribute>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IMegaAttribute[]>> {
    const options = createRequestOption(req);
    return this.http.get<IMegaAttribute[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getMegaAttributeIdentifier(megaAttribute: Pick<IMegaAttribute, 'id'>): number {
    return megaAttribute.id;
  }

  compareMegaAttribute(o1: Pick<IMegaAttribute, 'id'> | null, o2: Pick<IMegaAttribute, 'id'> | null): boolean {
    return o1 && o2 ? this.getMegaAttributeIdentifier(o1) === this.getMegaAttributeIdentifier(o2) : o1 === o2;
  }

  addMegaAttributeToCollectionIfMissing<Type extends Pick<IMegaAttribute, 'id'>>(
    megaAttributeCollection: Type[],
    ...megaAttributesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const megaAttributes: Type[] = megaAttributesToCheck.filter(isPresent);
    if (megaAttributes.length > 0) {
      const megaAttributeCollectionIdentifiers = megaAttributeCollection.map(megaAttributeItem =>
        this.getMegaAttributeIdentifier(megaAttributeItem),
      );
      const megaAttributesToAdd = megaAttributes.filter(megaAttributeItem => {
        const megaAttributeIdentifier = this.getMegaAttributeIdentifier(megaAttributeItem);
        if (megaAttributeCollectionIdentifiers.includes(megaAttributeIdentifier)) {
          return false;
        }
        megaAttributeCollectionIdentifiers.push(megaAttributeIdentifier);
        return true;
      });
      return [...megaAttributesToAdd, ...megaAttributeCollection];
    }
    return megaAttributeCollection;
  }
}
