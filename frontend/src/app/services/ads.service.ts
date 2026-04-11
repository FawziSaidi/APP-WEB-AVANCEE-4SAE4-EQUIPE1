import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdPlan, AdCampaign, CreateCampaignRequest } from '../pages/ads/models/ad.models';

@Injectable({ providedIn: 'root' })
export class AdsService {

  private readonly base = 'http://localhost:8222/api';

  constructor(private http: HttpClient) {}

  getPlans(): Observable<AdPlan[]> {
    return this.http.get<AdPlan[]>(`${this.base}/plans`);
  }

  getMyCampaigns(): Observable<AdCampaign[]> {
    return this.http.get<AdCampaign[]>(`${this.base}/campaigns/my`);
  }

  createCampaign(data: CreateCampaignRequest): Observable<AdCampaign> {
    return this.http.post<AdCampaign>(`${this.base}/campaigns`, data);
  }

  updateCampaign(id: number, data: CreateCampaignRequest): Observable<AdCampaign> {
    return this.http.put<AdCampaign>(`${this.base}/campaigns/${id}`, data);
  }

  deleteCampaign(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/campaigns/${id}`);
  }
}
