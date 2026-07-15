import { formatMoney, formatDate, formatRelativeTime, toMoneyString } from '../format';

describe('format utilities', () => {
  describe('formatMoney', () => {
    it('formats positive amounts with currency', () => {
      expect(formatMoney('1450.00', 'USD')).toMatch(/\$1,450\.00/);
    });

    it('handles zero', () => {
      expect(formatMoney('0', 'USD')).toMatch(/\$0\.00/);
    });

    it('handles decimal precision', () => {
      expect(formatMoney('10.5', 'USD')).toMatch(/\$10\.50/);
    });

    it('returns empty string for invalid input', () => {
      expect(formatMoney('abc', 'USD')).toBe('');
    });
  });

  describe('formatDate', () => {
    it('formats ISO date string', () => {
      const result = formatDate('2026-01-15T10:30:00Z');
      expect(result).toContain('Jan');
      expect(result).toContain('15');
      expect(result).toContain('2026');
    });

    it('returns empty for invalid date', () => {
      expect(formatDate('not-a-date')).toBe('');
    });
  });

  describe('formatRelativeTime', () => {
    it('returns "just now" for < 1 min', () => {
      const now = new Date().toISOString();
      expect(formatRelativeTime(now)).toBe('just now');
    });

    it('returns minutes ago', () => {
      const fiveMinsAgo = new Date(Date.now() - 5 * 60 * 1000).toISOString();
      expect(formatRelativeTime(fiveMinsAgo)).toBe('5m ago');
    });

    it('returns hours ago', () => {
      const threeHoursAgo = new Date(Date.now() - 3 * 60 * 60 * 1000).toISOString();
      expect(formatRelativeTime(threeHoursAgo)).toBe('3h ago');
    });

    it('returns days ago', () => {
      const twoDaysAgo = new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString();
      expect(formatRelativeTime(twoDaysAgo)).toBe('2d ago');
    });

    it('falls back to formatted date for > 7 days', () => {
      const tenDaysAgo = new Date(Date.now() - 10 * 24 * 60 * 60 * 1000).toISOString();
      expect(formatRelativeTime(tenDaysAgo)).toMatch(/Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec/);
    });
  });

  describe('toMoneyString', () => {
    it('formats number to 2 decimal places', () => {
      expect(toMoneyString(10)).toBe('10.00');
      expect(toMoneyString(10.5)).toBe('10.50');
      expect(toMoneyString('10.555')).toBe('10.55'); // truncates, not rounds
    });

    it('returns 0.00 for invalid', () => {
      expect(toMoneyString('abc')).toBe('0.00');
      expect(toMoneyString(NaN)).toBe('0.00');
    });
  });
});