import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardLayoutComponent } from './components/dashboard-layout/dashboard-layout.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { CalendarViewComponent } from './components/calendar-view/calendar-view.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { ProfileComponent } from './components/profile/profile.component';
import { DatePageComponent } from './components/date-page/date-page.component';
const routes: Routes = [
  {
    path: '',
    component: DashboardLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'main',
        pathMatch: 'full'
      },
      {
        path: 'main',
        component: MainPageComponent
      },
      {
        path: 'calendar',
        component: CalendarViewComponent
      },
      {
        path: 'date/:date',
        component: DatePageComponent
      },
      {
        path: 'analytics',
        component: AnalyticsComponent
      },
      {
        path: 'profile',
        component: ProfileComponent
      }
    ]
  }
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }