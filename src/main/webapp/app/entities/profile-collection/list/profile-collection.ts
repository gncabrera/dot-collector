import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ProfileCollectionDeleteDialog } from '../delete/profile-collection-delete-dialog';
import { IProfileCollection } from '../profile-collection.model';
import { ProfileCollectionService } from '../service/profile-collection.service';

@Component({
  selector: 'jhi-profile-collection',
  templateUrl: './profile-collection.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class ProfileCollection implements OnInit {
  subscription: Subscription | null = null;
  readonly profileCollections = signal<IProfileCollection[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly profileCollectionService = inject(ProfileCollectionService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.profileCollectionService.profileCollectionsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.profileCollections.set(this.fillComponentAttributesFromResponseBody([...this.profileCollectionService.profileCollections()]));
    });
  }

  trackId = (item: IProfileCollection): number => this.profileCollectionService.getProfileCollectionIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.profileCollections().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(profileCollection: IProfileCollection): void {
    const modalRef = this.modalService.open(ProfileCollectionDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profileCollection = profileCollection;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: IProfileCollection[]): IProfileCollection[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IProfileCollection[]): IProfileCollection[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.profileCollectionService.profileCollectionsParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
