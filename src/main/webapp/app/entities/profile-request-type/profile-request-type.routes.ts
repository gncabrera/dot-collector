import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProfileRequestTypeResolve from './route/profile-request-type-routing-resolve.service';

const profileRequestTypeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/profile-request-type').then(m => m.ProfileRequestType),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/profile-request-type-detail').then(m => m.ProfileRequestTypeDetail),
    resolve: {
      profileRequestType: ProfileRequestTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/profile-request-type-update').then(m => m.ProfileRequestTypeUpdate),
    resolve: {
      profileRequestType: ProfileRequestTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/profile-request-type-update').then(m => m.ProfileRequestTypeUpdate),
    resolve: {
      profileRequestType: ProfileRequestTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default profileRequestTypeRoute;
