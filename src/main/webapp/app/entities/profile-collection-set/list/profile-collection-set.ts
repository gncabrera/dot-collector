import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatePipe } from 'app/shared/date';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ProfileCollectionSetDeleteDialog } from '../delete/profile-collection-set-delete-dialog';
import { IProfileCollectionSet } from '../profile-collection-set.model';
import { ProfileCollectionSetService } from '../service/profile-collection-set.service';

@Component({
  selector: 'jhi-profile-collection-set',
  templateUrl: './profile-collection-set.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatePipe],
})
export class ProfileCollectionSet implements OnInit {
  subscription: Subscription | null = null;
  readonly profileCollectionSets = signal<IProfileCollectionSet[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly profileCollectionSetService = inject(ProfileCollectionSetService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.profileCollectionSetService.profileCollectionSetsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.profileCollectionSets.set(
        this.fillComponentAttributesFromResponseBody([...this.profileCollectionSetService.profileCollectionSets()]),
      );
    });
  }

  trackId = (item: IProfileCollectionSet): number => this.profileCollectionSetService.getProfileCollectionSetIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.profileCollectionSets().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(profileCollectionSet: IProfileCollectionSet): void {
    const modalRef = this.modalService.open(ProfileCollectionSetDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profileCollectionSet = profileCollectionSet;
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

  protected refineData(data: IProfileCollectionSet[]): IProfileCollectionSet[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IProfileCollectionSet[]): IProfileCollectionSet[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.profileCollectionSetService.profileCollectionSetsParams.set(queryObject);
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
