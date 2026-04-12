import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { SubscriptionsRoutingModule } from './subscriptions-routing.module';
import { SubscriptionListComponent } from './subscription-list/subscription-list.component';
import { SubscriptionFormComponent } from './subscription-form/subscription-form.component';
import { SubscriptionStatsComponent } from './subscription-stats/subscription-stats.component';
import { ChurnPredictionComponent } from './churn-prediction/churn-prediction.component';
import { PromoManagementComponent } from './promo-management/promo-management.component';

@NgModule({
  declarations: [
    SubscriptionListComponent,
    SubscriptionFormComponent,
    SubscriptionStatsComponent,
    ChurnPredictionComponent,
    PromoManagementComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    SubscriptionsRoutingModule
  ]
})
export class SubscriptionsModule { }