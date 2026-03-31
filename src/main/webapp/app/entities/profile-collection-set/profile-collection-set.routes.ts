import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfileCollectionSetResolve from './route/profile-collection-set-routing-resolve.service';

const profileCollectionSetRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/profile-collection-set').then(m => m.ProfileCollectionSet),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/profile-collection-set-detail').then(m => m.ProfileCollectionSetDetail),
    resolve: {
      profileCollectionSet: ProfileCollectionSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/profile-collection-set-update').then(m => m.ProfileCollectionSetUpdate),
    resolve: {
      profileCollectionSet: ProfileCollectionSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/profile-collection-set-update').then(m => m.ProfileCollectionSetUpdate),
    resolve: {
      profileCollectionSet: ProfileCollectionSetResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default profileCollectionSetRoute;
