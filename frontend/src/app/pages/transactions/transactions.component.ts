import { Component, OnInit } from '@angular/core';
import { TransactionService, Transaction, TransactionRequest } from '../../services/transaction.service';

@Component({
  selector: 'app-transactions',
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {

  // Form
  receiverId = '';
  amount: number | null = null;
  currency = 'EUR';
  reference = '';

  // State
  transactions: Transaction[] = [];
  isLoading = false;
  isSending = false;
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  constructor(private txService: TransactionService) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.isLoading = true;
    this.txService.getHistory().subscribe({
      next: (data) => { this.transactions = data; this.isLoading = false; },
      error: () => { this.transactions = []; this.isLoading = false; }
    });
  }

  sendTransaction(): void {
    if (!this.receiverId.trim() || !this.amount || this.amount <= 0) return;

    this.isSending = true;
    const payload: TransactionRequest = {
      receiver_id: this.receiverId.trim(),
      amount: this.amount,
      currency: this.currency,
      reference: this.reference || 'Payment'
    };

    this.txService.sendTransaction(payload).subscribe({
      next: () => {
        this.isSending = false;
        this.displayToast('Transaction sent successfully!', 'success');
        this.resetForm();
        this.loadHistory();
      },
      error: (err) => {
        this.isSending = false;
        const msg = err?.error?.detail || 'Transaction failed. Please try again.';
        this.displayToast(msg, 'error');
      }
    });
  }

  private resetForm(): void {
    this.receiverId = '';
    this.amount = null;
    this.reference = '';
  }

  private displayToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 4000);
  }
}
