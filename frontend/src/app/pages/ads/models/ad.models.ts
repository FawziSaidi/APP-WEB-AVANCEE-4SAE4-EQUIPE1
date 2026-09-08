export type AdType = 'BANNER' | 'FEATURED_PROFILE' | 'JOB_BOOST';
export type AdLocation = 'LANDING_PAGE' | 'JOB_FEED' | 'SEARCH_SIDEBAR';
export type CampaignStatus = 'PENDING' | 'ACTIVE' | 'REJECTED' | 'EXPIRED';
export type RoleType = 'FREELANCER' | 'CLIENT';

export interface AdPlan {
  readonly id: number;
  readonly name: string;
  readonly type: AdType;
  readonly price: number;
  readonly location: AdLocation;
  readonly roleType: RoleType;
  readonly description?: string;
  icon?: string;
}

export interface AdCampaign {
  readonly id: number;
  readonly userId: number;
  readonly planId: number;
  readonly title: string;
  readonly description: string;
  readonly imageUrl: string;
  readonly targetUrl: string;
  status: CampaignStatus;
  rejectionReason?: string;
  readonly createdAt: Date | string;
  roleType: RoleType;
  readonly planName?: string;
  readonly planType?: AdType;
  readonly planLocation?: AdLocation;
  readonly views?: number;
  readonly clicks?: number;
}

export interface CreateCampaignRequest {
  readonly planId: number;
  readonly title: string;
  readonly description: string;
  readonly imageUrl: string;
  readonly targetUrl: string;
  readonly roleType: RoleType;
}
