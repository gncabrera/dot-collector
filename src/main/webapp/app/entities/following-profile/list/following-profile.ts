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
import { FollowingProfileDeleteDialog } from '../delete/following-profile-delete-dialog';
import { IFollowingProfile } from '../following-profile.model';
import { FollowingProfileService } from '../service/following-profile.service';

@Component({
  selector: 'jhi-following-profile',
  templateUrl: './following-profile.html',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, SortDirective, SortByDirective, FormatMediumDatePipe],
})
export class FollowingProfile implements OnInit {
  subscription: Subscription | null = null;
  readonly followingProfiles = signal<IFollowingProfile[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly followingProfileService = inject(FollowingProfileService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.followingProfileService.followingProfilesResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.followingProfiles.set(this.fillComponentAttributesFromResponseBody([...this.followingProfileService.followingProfiles()]));
    });
  }

  trackId = (item: IFollowingProfile): number => this.followingProfileService.getFollowingProfileIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.followingProfiles().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(followingProfile: IFollowingProfile): void {
    const modalRef = this.modalService.open(FollowingProfileDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.followingProfile = followingProfile;
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

  protected refineData(data: IFollowingProfile[]): IFollowingProfile[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: IFollowingProfile[]): IFollowingProfile[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.followingProfileService.followingProfilesParams.set(queryObject);
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
