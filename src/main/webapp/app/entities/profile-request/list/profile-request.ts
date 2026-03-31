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
import { ProfileRequestDeleteDialog } from '../delete/profile-request-delete-dialog';
import { IProfileRequest } from '../profile-request.model';
import { ProfileRequestService } from '../service/profile-request.service';

@Component({
  selector: 'jhi-profile-request',
  templateUrl: './profile-request.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class ProfileRequest implements OnInit {
  subscription: Subscription | null = null;
  readonly profileRequests = signal<IProfileRequest[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly profileRequestService = inject(ProfileRequestService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.profileRequestService.profileRequestsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.profileRequests.set(this.fillComponentAttributesFromResponseBody([...this.profileRequestService.profileRequests()]));
    });
  }

  trackId = (item: IProfileRequest): number => this.profileRequestService.getProfileRequestIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.profileRequests().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(profileRequest: IProfileRequest): void {
    const modalRef = this.modalService.open(ProfileRequestDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profileRequest = profileRequest;
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

  protected refineData(data: IProfileRequest[]): IProfileRequest[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IProfileRequest[]): IProfileRequest[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.profileRequestService.profileRequestsParams.set(queryObject);
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
