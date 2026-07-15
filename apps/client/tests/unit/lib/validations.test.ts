import { registerSchema, loginSchema, profileUpdateSchema, createRoomSchema } from '../lib/validations';

describe('validation schemas', () => {
  describe('registerSchema', () => {
    it('accepts valid registration', () => {
      const result = registerSchema.safeParse({
        email: 'user@example.com',
        password: 'password123',
        displayName: 'John Doe',
      });
      expect(result.success).toBe(true);
    });

    it('rejects invalid email', () => {
      const result = registerSchema.safeParse({
        email: 'not-an-email',
        password: 'password123',
        displayName: 'John',
      });
      expect(result.success).toBe(false);
      expect(result.error.flatten().fieldErrors.email).toContain('valid email');
    });

    it('rejects short password', () => {
      const result = registerSchema.safeParse({
        email: 'user@example.com',
        password: '123',
        displayName: 'John',
      });
      expect(result.success).toBe(false);
      expect(result.error.flatten().fieldErrors.password).toContain('At least 8 characters');
    });

    it('rejects short display name', () => {
      const result = registerSchema.safeParse({
        email: 'user@example.com',
        password: 'password123',
        displayName: 'J',
      });
      expect(result.success).toBe(false);
    });
  });

  describe('loginSchema', () => {
    it('accepts valid login', () => {
      const result = loginSchema.safeParse({
        email: 'user@example.com',
        password: 'password123',
      });
      expect(result.success).toBe(true);
    });

    it('requires email', () => {
      const result = loginSchema.safeParse({
        password: 'password123',
      });
      expect(result.success).toBe(false);
    });

    it('requires password', () => {
      const result = loginSchema.safeParse({
        email: 'user@example.com',
      });
      expect(result.success).toBe(false);
    });
  });

  describe('profileUpdateSchema', () => {
    it('accepts partial update', () => {
      const result = profileUpdateSchema.safeParse({
        bio: 'Updated bio',
        city: 'San Francisco',
      });
      expect(result.success).toBe(true);
    });

    it('validates state length', () => {
      const result = profileUpdateSchema.safeParse({
        state: 'CAL', // should be 2 chars
      });
      expect(result.success).toBe(false);
    });

    it('validates archetype enum', () => {
      const result = profileUpdateSchema.safeParse({
        archetype: 'invalid',
      });
      expect(result.success).toBe(false);
    });

    it('accepts valid archetype', () => {
      const result = profileUpdateSchema.safeParse({
        archetype: 'professional',
      });
      expect(result.success).toBe(true);
    });
  });

  describe('createRoomSchema', () => {
    const validRoom = {
      title: 'Nice room near transit',
      description: 'Spacious private room in a 2BR apartment, 5 min walk to Caltrain.',
      monthlyRent: '1450.00',
      securityDeposit: '1450.00',
      city: 'Sunnyvale',
      state: 'CA',
      zip: '94085',
      roomType: 'private',
      propertyType: 'apartment',
      furnished: 'furnished',
      availableDate: '2026-02-01',
      leaseDurationMonths: 12,
      utilitiesIncluded: true,
      amenities: ['wifi', 'laundry', 'gym'],
      parking: true,
      petPolicy: 'cats',
      smokingPolicy: 'no_smoking',
      occupancy: 1,
      genderPreference: 'any',
      contactPreference: 'in_app',
      images: ['https://example.com/img1.jpg'],
    };

    it('accepts valid room', () => {
      const result = createRoomSchema.safeParse(validRoom);
      expect(result.success).toBe(true);
    });

    it('rejects missing required fields', () => {
      const { title, ...rest } = validRoom;
      const result = createRoomSchema.safeParse(rest);
      expect(result.success).toBe(false);
    });

    it('rejects invalid rent format', () => {
      const result = createRoomSchema.safeParse({ ...validRoom, monthlyRent: 'abc' });
      expect(result.success).toBe(false);
    });

    it('rejects negative rent', () => {
      const result = createRoomSchema.safeParse({ ...validRoom, monthlyRent: '-100' });
      expect(result.success).toBe(false);
    });

    it('rejects invalid state length', () => {
      const result = createRoomSchema.safeParse({ ...validRoom, state: 'California' });
      expect(result.success).toBe(false);
    });
  });
});