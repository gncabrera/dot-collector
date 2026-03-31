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
import { ProfileRequestTypeDeleteDialog } from '../delete/profile-request-type-delete-dialog';
import { IProfileRequestType } from '../profile-request-type.model';
import { ProfileRequestTypeService } from '../service/profile-request-type.service';

@Component({
  selector: 'jhi-profile-request-type',
  templateUrl: './profile-request-type.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective],
})
export class ProfileRequestType implements OnInit {
  subscription: Subscription | null = null;
  readonly profileRequestTypes = signal<IProfileRequestType[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly profileRequestTypeService = inject(ProfileRequestTypeService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.profileRequestTypeService.profileRequestTypesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.profileRequestTypes.set(this.fillComponentAttributesFromResponseBody([...this.profileRequestTypeService.profileRequestTypes()]));
    });
  }

  trackId = (item: IProfileRequestType): number => this.profileRequestTypeService.getProfileRequestTypeIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.profileRequestTypes().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(profileRequestType: IProfileRequestType): void {
    const modalRef = this.modalService.open(ProfileRequestTypeDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.profileRequestType = profileRequestType;
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

  protected refineData(data: IProfileRequestType[]): IProfileRequestType[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IProfileRequestType[]): IProfileRequestType[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.profileRequestTypeService.profileRequestTypesParams.set(queryObject);
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
