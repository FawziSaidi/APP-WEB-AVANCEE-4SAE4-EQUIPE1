import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserSubscription } from '../models/user-subscription.model';

@Injectable({
  providedIn: 'root'
})
export class UserSubscriptionService {

  private readonly baseUrl = 'http://localhost:8222/api/user-subscriptions';

  constructor(private http: HttpClient) {}

  getMySubscription(): Observable<UserSubscription> {
    return this.http.get<UserSubscription>(`${this.baseUrl}/my-subscription`);
  }

  getAll(): Observable<UserSubscription[]> {
    return this.http.get<UserSubscription[]>(this.baseUrl);
  }

  getById(id: number): Observable<UserSubscription> {
    return this.http.get<UserSubscription>(`${this.baseUrl}/${id}`);
  }

  subscribe(subscriptionId: number, promoCode?: string): Observable<UserSubscription> {
    return this.http.post<UserSubscription>(`${this.baseUrl}/subscribe`, {
      subscriptionId,
      promoCode
    });
  }

  cancel(id: number): Observable<UserSubscription> {
    return this.http.put<UserSubscription>(`${this.baseUrl}/${id}/cancel`, {});
  }

  toggleAutoRenew(id: number): Observable<UserSubscription> {
    return this.http.put<UserSubscription>(`${this.baseUrl}/${id}/toggle-auto-renew`, {});
  }

  getActiveByUserId(userId: number): Observable<UserSubscription> {
    return this.http.get<UserSubscription>(`${this.baseUrl}/user/${userId}/active`);
  }
}