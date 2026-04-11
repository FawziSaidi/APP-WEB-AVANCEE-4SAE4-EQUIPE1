import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TransactionRequest {
  receiver_id: string;
  amount: number;
  currency: string;
  reference: string;
}

export interface Transaction {
  id: string;
  sender_id: string;
  receiver_id: string;
  amount: number;
  currency: string;
  status: string;
  reference: string;
  created_at: string;
}

@Injectable({ providedIn: 'root' })
export class TransactionService {

  private readonly base = 'http://localhost:8222/api/transactions';

  constructor(private http: HttpClient) {}

  sendTransaction(data: TransactionRequest): Observable<Transaction> {
    return this.http.post<Transaction>(`${this.base}/send`, data);
  }

  getHistory(): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.base}/history`);
  }
}
