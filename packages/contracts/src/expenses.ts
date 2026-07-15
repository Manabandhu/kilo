import type { UUID, ISODateTime, ISODate } from "./common";

export type SplitStrategy = "equal" | "exact" | "percentage" | "shares";

export interface ExpenseGroup {
  id: UUID;
  name: string;
  currency: string;
  memberIds: UUID[];
  createdById: UUID;
  totalOwed: string; // decimal string, never float
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface ExpenseSplit {
  userId: UUID;
  amount: string; // decimal string for exact
  percent?: number; // for percentage
  shares?: number; // for shares
}

export interface Expense {
  id: UUID;
  groupId: UUID;
  title: string;
  description?: string;
  totalAmount: string; // decimal string
  currency: string;
  paidByIds: UUID[]; // multi-payer support
  splitStrategy: SplitStrategy;
  splits: ExpenseSplit[];
  date: ISODate;
  category?: string;
  receiptUrl?: string;
  createdById: UUID;
  createdAt: ISODateTime;
}

export interface CreateExpenseRequest {
  title: string;
  description?: string;
  totalAmount: string;
  currency: string;
  paidByIds: UUID[];
  splitStrategy: SplitStrategy;
  splits: ExpenseSplit[];
  date: ISODate;
  category?: string;
  receiptUrl?: string;
}

export interface Settlement {
  id: UUID;
  groupId: UUID;
  fromUserId: UUID;
  toUserId: UUID;
  amount: string; // decimal string
  currency: string;
  status: "pending" | "completed";
  note?: string;
  recordedById: UUID;
  createdAt: ISODateTime;
}

export interface RecordSettlementRequest {
  fromUserId: UUID;
  toUserId: UUID;
  amount: string;
  note?: string;
}

export interface BalanceSummary {
  groupId: UUID;
  currency: string;
  balances: { userId: UUID; net: string }[]; // positive => owed to, negative => owes
}
