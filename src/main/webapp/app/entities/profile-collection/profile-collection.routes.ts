import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfileCollectionResolve from './route/profile-collection-routing-resolve.service';

const profileCollectionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/profile-collection').then(m => m.ProfileCollection),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/profile-collection-detail').then(m => m.ProfileCollectionDetail),
    resolve: {
      profileCollection: ProfileCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/profile-collection-update').then(m => m.ProfileCollectionUpdate),
    resolve: {
      profileCollection: ProfileCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/profile-collection-update').then(m => m.ProfileCollectionUpdate),
    resolve: {
      profileCollection: ProfileCollectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default profileCollectionRoute;
