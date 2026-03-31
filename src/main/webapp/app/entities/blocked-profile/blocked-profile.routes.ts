import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import BlockedProfileResolve from './route/blocked-profile-routing-resolve.service';

const blockedProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/blocked-profile').then(m => m.BlockedProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/blocked-profile-detail').then(m => m.BlockedProfileDetail),
    resolve: {
      blockedProfile: BlockedProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/blocked-profile-update').then(m => m.BlockedProfileUpdate),
    resolve: {
      blockedProfile: BlockedProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/blocked-profile-update').then(m => m.BlockedProfileUpdate),
    resolve: {
      blockedProfile: BlockedProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default blockedProfileRoute;
