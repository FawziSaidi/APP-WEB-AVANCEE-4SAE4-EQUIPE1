import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.services';
import { AdsService } from '../../services/ads.service';
import { AdPlan, AdCampaign, CampaignStatus, RoleType, CreateCampaignRequest } from './models/ad.models';

@Component({
  selector: 'app-ad-center',
  templateUrl: './ad-center.component.html',
  styleUrls: ['./ad-center.component.scss']
})
export class AdCenterComponent implements OnInit {
  currentRole: RoleType = 'FREELANCER';

  isLoading = false;
  plansLoading = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  showCreateModal = false;
  isEditing = false;
  editingCampaignId: number | null = null;

  showDeleteConfirm = false;
  deletingCampaignId: number | null = null;

  selectedPlanId: number | null = null;
  formTitle = '';
  formDescription = '';
  formImageUrl = '';
  formTargetUrl = '';

  adPlans: AdPlan[] = [];
  campaigns: AdCampaign[] = [];

  constructor(
    private authService: AuthService,
    private adsService: AdsService
  ) {}

  ngOnInit(): void {
    const role = this.authService.getRole()?.toUpperCase();
    if (role === 'FREELANCER' || role === 'CLIENT') {
      this.currentRole = role;
    }
    this.loadPlans();
    this.loadCampaigns();
  }

  get rolePlans(): AdPlan[] {
    // Show plans matching current role; if none match, show all plans
    const filtered = this.adPlans.filter(p => p.roleType === this.currentRole);
    return filtered.length > 0 ? filtered : this.adPlans;
  }

  statusBadgeClass(status: CampaignStatus): string {
    switch (status) {
      case 'PENDING': return 'badge-warning';
      case 'ACTIVE': return 'badge-success';
      case 'REJECTED': return 'badge-danger';
      case 'EXPIRED': return 'badge-expired';
      default: return '';
    }
  }

  // ── CREATE ──
  openCreateModal(): void {
    this.isEditing = false;
    this.editingCampaignId = null;
    this.resetForm();
    this.showCreateModal = true;
  }

  // ── EDIT ──
  openEditModal(campaign: AdCampaign): void {
    this.isEditing = true;
    this.editingCampaignId = campaign.id;
    this.selectedPlanId = campaign.planId;
    this.formTitle = campaign.title;
    this.formDescription = campaign.description;
    this.formImageUrl = campaign.imageUrl;
    this.formTargetUrl = campaign.targetUrl;
    this.showCreateModal = true;
  }

  closeModal(): void {
    this.showCreateModal = false;
    this.resetForm();
  }

  selectPlan(planId: number): void {
    this.selectedPlanId = planId;
  }

  get selectedPlan(): AdPlan | undefined {
    return this.adPlans.find(p => p.id === this.selectedPlanId);
  }

  submitCampaign(): void {
    const plan = this.selectedPlan;
    if (!plan || !this.formTitle.trim()) return;

    const payload: CreateCampaignRequest = {
      planId: plan.id,
      title: this.formTitle,
      description: this.formDescription,
      imageUrl: this.formImageUrl?.trim() || '',
      targetUrl: this.formTargetUrl,
      roleType: this.currentRole
    };

    if (this.isEditing && this.editingCampaignId !== null) {
      this.adsService.updateCampaign(this.editingCampaignId, payload).subscribe({
        next: () => { this.closeModal(); this.displayToast('Campaign updated!', 'success'); this.loadCampaigns(); },
        error: () => { this.closeModal(); this.displayToast('Failed to update campaign.', 'error'); }
      });
    } else {
      this.adsService.createCampaign(payload).subscribe({
        next: () => { this.closeModal(); this.displayToast('Campaign created!', 'success'); this.loadCampaigns(); },
        error: () => { this.closeModal(); this.displayToast('Failed to create campaign.', 'error'); }
      });
    }
  }

  // ── DELETE ──
  confirmDelete(campaignId: number): void {
    this.deletingCampaignId = campaignId;
    this.showDeleteConfirm = true;
  }

  cancelDelete(): void {
    this.showDeleteConfirm = false;
    this.deletingCampaignId = null;
  }

  deleteCampaign(): void {
    if (this.deletingCampaignId !== null) {
      const id = this.deletingCampaignId;
      this.showDeleteConfirm = false;
      this.deletingCampaignId = null;
      this.adsService.deleteCampaign(id).subscribe({
        next: () => { this.displayToast('Campaign deleted.', 'success'); this.loadCampaigns(); },
        error: () => { this.displayToast('Failed to delete campaign.', 'error'); }
      });
    }
  }

  canEdit(campaign: AdCampaign): boolean {
    return campaign.status === 'PENDING' || campaign.status === 'REJECTED';
  }

  // ── DATA LOADING ──
  private loadCampaigns(): void {
    this.isLoading = true;
    this.adsService.getMyCampaigns().subscribe({
      next: (data) => { this.campaigns = data; this.isLoading = false; },
      error: () => { this.campaigns = []; this.isLoading = false; }
    });
  }

  private loadPlans(): void {
    this.plansLoading = true;
    this.adsService.getPlans().subscribe({
      next: (plans) => { this.adPlans = plans; this.plansLoading = false; },
      error: () => { this.adPlans = []; this.plansLoading = false; }
    });
  }

  private resetForm(): void {
    this.selectedPlanId = null;
    this.formTitle = '';
    this.formDescription = '';
    this.formImageUrl = '';
    this.formTargetUrl = '';
    this.isEditing = false;
    this.editingCampaignId = null;
  }

  private displayToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 4000);
  }
}
