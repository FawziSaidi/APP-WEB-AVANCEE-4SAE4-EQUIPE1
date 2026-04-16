import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CourseSummary, CourseDetail, CourseRequest,
  LessonResponse, LessonRequest,
  ReviewResponse, NoteResponse, CertificateResponse,
  CouponValidateResponse, LessonHistoryRecord, AdminPaymentRecord, CourseProgress
} from '../frontoffice/cours/cours/cours.models';

export interface PaymentRequest {
  userId: number;
  courseId: number;
  method: 'CARD' | 'PAYPAL' | 'BANK_TRANSFER';
  couponCode?: string;
}

export interface ProgressRequest {
  lessonId: number;
  userId: number;
  timeSpentSeconds: number;
  notes?: string;
  completedAt: string;
}

export interface ProgressResponse {
  message: string;
  certificateUnlocked: boolean;
}

export interface LessonHistoryPage {
  records: LessonHistoryRecord[];
  total: number;
}

export interface ReviewRequest {
  userId: number;
  courseId: number;
  rating: number;
  comment: string;
}

@Injectable({ providedIn: 'root' })
export class CoursService {

  private base       = 'http://localhost:8222';  // API Gateway (Eureka)
  private api        = `${this.base}/api/courses`;
  private publicApi  = `${this.base}/api/courses/public`;
  private lessonBase = `${this.base}`;           // Lessons passent aussi par la gateway

  constructor(private http: HttpClient) {}

  // ── Public Courses ──────────────────────────────────────────────────────────

  getPublishedCourses(category?: string): Observable<CourseSummary[]> {
    let params = new HttpParams();
    if (category) params = params.set('category', category);
    return this.http.get<CourseSummary[]>(`${this.publicApi}/published`, { params }); // ← FIXED
  }

  getCourseById(id: number): Observable<CourseDetail> {
    return this.http.get<CourseDetail>(`${this.publicApi}/${id}`); // ← FIXED
  }

  // ── Lessons ─────────────────────────────────────────────────────────────────

  getLessons(courseId: number, userId: number): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.api}/${courseId}/lessons`, {
      params: new HttpParams().set('userId', userId)
    });
  }

  completeLesson(lessonId: number, userId: number): Observable<void> {
    return this.http.patch<void>(`${this.base}/api/lessons/${lessonId}/complete`, null, {
      params: new HttpParams().set('userId', userId)
    });
  }

  // ── Enroll ──────────────────────────────────────────────────────────────────

  enroll(courseId: number, userId: number): Observable<void> {
    return this.http.post<void>(`${this.api}/${courseId}/enroll`, { userId });
  }

  // ── Coupon ──────────────────────────────────────────────────────────────────

  validateCoupon(courseId: number, code: string): Observable<CouponValidateResponse> {
    return this.http.get<CouponValidateResponse>(`${this.base}/api/coupons/validate`, {
      params: new HttpParams().set('courseId', courseId).set('code', code)
    });
  }

  // ── Payment ─────────────────────────────────────────────────────────────────

  initiatePayment(payload: PaymentRequest): Observable<void> {
    return this.http.post<void>(`${this.base}/api/payments/initiate`, payload);
  }

  // ── Progress ────────────────────────────────────────────────────────────────

  submitProgress(payload: ProgressRequest): Observable<ProgressResponse> {
    return this.http.post<ProgressResponse>(`${this.base}/api/progress/submit`, payload);
  }

  // ── Notes ───────────────────────────────────────────────────────────────────

  getUserNotes(userId: number): Observable<NoteResponse[]> {
    return this.http.get<NoteResponse[]>(`${this.base}/api/users/${userId}/notes`);
  }

  saveNote(lessonId: number, userId: number, content: string): Observable<NoteResponse> {
    return this.http.post<NoteResponse>(`${this.base}/api/lessons/${lessonId}/notes`, { userId, content });
  }

  // ── Reviews ─────────────────────────────────────────────────────────────────

  getCourseReviews(courseId: number): Observable<ReviewResponse[]> {
    return this.http.get<ReviewResponse[]>(`${this.publicApi}/${courseId}/reviews`); // ← FIXED
  }

  submitReview(payload: ReviewRequest): Observable<void> {
    return this.http.post<void>(`${this.base}/api/reviews`, payload);
  }

  markReviewHelpful(reviewId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/api/reviews/${reviewId}/helpful`, null);
  }

  // ── Bookmarks ────────────────────────────────────────────────────────────────

  bookmarkLesson(lessonId: number, userId: number): Observable<void> {
    return this.http.post<void>(`${this.base}/api/lessons/${lessonId}/bookmark`, { userId });
  }

  removeBookmark(lessonId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/api/lessons/${lessonId}/bookmark`, {
      body: { userId }
    });
  }

  // ── Certificates ─────────────────────────────────────────────────────────────

  getUserCertificates(userId: number): Observable<CertificateResponse[]> {
    return this.http.get<CertificateResponse[]>(`${this.base}/api/users/${userId}/certificates`);
  }

  // ── Admin: Courses ────────────────────────────────────────────────────────────

  getAllCoursesAdmin(): Observable<CourseDetail[]> {
    return this.http.get<CourseDetail[]>(`${this.base}/api/admin/courses`);
  }

  createCourse(data: CourseRequest, adminId: number): Observable<CourseDetail> {
    return this.http.post<CourseDetail>(this.api, data, {
      params: new HttpParams().set('createdBy', adminId)
    });
  }

  updateCourse(id: number, data: CourseRequest): Observable<CourseDetail> {
    return this.http.put<CourseDetail>(`${this.api}/${id}`, data);
  }

  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  // ── Admin: Lessons ────────────────────────────────────────────────────────────
  // Les lessons sont dans le lesson-service, routées via la gateway

  getLessonsAdmin(courseId: number): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>(`${this.lessonBase}/api/lessons/course/${courseId}`);
  }

  addLesson(courseId: number, data: LessonRequest): Observable<LessonResponse> {
    return this.http.post<LessonResponse>(`${this.lessonBase}/api/lessons/course/${courseId}`, data);
  }

  updateLesson(lessonId: number, data: LessonRequest): Observable<LessonResponse> {
    return this.http.put<LessonResponse>(`${this.lessonBase}/api/lessons/${lessonId}`, data);
  }

  deleteLesson(lessonId: number): Observable<void> {
    return this.http.delete<void>(`${this.lessonBase}/api/lessons/${lessonId}`);
  }

  // ── Admin: History ────────────────────────────────────────────────────────────

  getAdminLessonHistory(
    courseId?: number,
    userId?: number,
    dateFrom?: string,
    dateTo?: string,
    page = 1,
    pageSize = 20
  ): Observable<LessonHistoryPage> {
    let params = new HttpParams()
      .set('page', page)
      .set('pageSize', pageSize);
    if (courseId != null) params = params.set('courseId', courseId);
    if (userId   != null) params = params.set('userId',   userId);
    if (dateFrom)         params = params.set('dateFrom', dateFrom);
    if (dateTo)           params = params.set('dateTo',   dateTo);
    return this.http.get<LessonHistoryPage>(`${this.base}/api/admin/lesson-history`, { params });
  }

  exportLessonHistoryCsv(courseId?: number): Observable<Blob> {
    let params = new HttpParams();
    if (courseId != null) params = params.set('courseId', courseId);
    return this.http.get(`${this.base}/api/admin/lesson-history/export`, {
      params,
      responseType: 'blob'
    });
  }

  // ── Admin: Payments ───────────────────────────────────────────────────────────

  getAdminPayments(courseId?: number): Observable<AdminPaymentRecord[]> {
    let params = new HttpParams();
    if (courseId != null) params = params.set('courseId', courseId);
    return this.http.get<AdminPaymentRecord[]>(`${this.base}/api/admin/payments`, { params });
  }

  // ── Admin: Progress ───────────────────────────────────────────────────────────

  getUserProgressAdmin(courseId?: number): Observable<CourseProgress[]> {
    let params = new HttpParams();
    if (courseId != null) params = params.set('courseId', courseId);
    return this.http.get<CourseProgress[]>(`${this.base}/api/admin/user-progress`, { params });
  }
}