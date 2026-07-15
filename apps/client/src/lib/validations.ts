import { z } from "zod";

export const emailSchema = z.string().email("Enter a valid email");
export const passwordSchema = z.string().min(8, "At least 8 characters");

export const registerSchema = z.object({
  email: emailSchema,
  password: passwordSchema,
  displayName: z.string().min(2, "Enter your name").max(60),
});
export type RegisterInput = z.infer<typeof registerSchema>;

export const loginSchema = z.object({
  email: emailSchema,
  password: z.string().min(1, "Password is required"),
});
export type LoginInput = z.infer<typeof loginSchema>;

export const profileUpdateSchema = z.object({
  displayName: z.string().min(2).max(60).optional(),
  bio: z.string().max(2000).optional(),
  city: z.string().max(80).optional(),
  state: z.string().length(2).optional(),
  languages: z.array(z.string()).optional(),
  occupation: z.string().max(80).optional(),
  archetype: z.enum(["student", "professional", "family", "newcomer"]).optional(),
  university: z.string().max(120).optional(),
  employer: z.string().max(120).optional(),
  interests: z.array(z.string()).optional(),
});
export type ProfileUpdateInput = z.infer<typeof profileUpdateSchema>;

export const createRoomSchema = z.object({
  title: z.string().min(4).max(120),
  description: z.string().min(10).max(4000),
  monthlyRent: z.string().regex(/^\d+(\.\d{1,2})?$/, "Enter a valid amount"),
  securityDeposit: z.string().regex(/^\d+(\.\d{1,2})?$/).optional(),
  city: z.string().min(1),
  state: z.string().length(2),
  zip: z.string().max(10).optional(),
  approximateLocation: z.object({ lat: z.number(), lng: z.number() }).optional(),
  exactAddressShared: z.boolean().optional(),
  roomType: z.string().min(1),
  propertyType: z.string().min(1),
  furnished: z.string().min(1),
  availableDate: z.string().optional(),
  leaseDurationMonths: z.number().int().positive().optional(),
  utilitiesIncluded: z.boolean(),
  amenities: z.array(z.string()),
  parking: z.boolean(),
  petPolicy: z.string(),
  smokingPolicy: z.string(),
  occupancy: z.number().int().positive(),
  genderPreference: z.string(),
  contactPreference: z.string(),
  images: z.array(z.string()),
});
export type CreateRoomInput = z.infer<typeof createRoomSchema>;

export const createRideSchema = z.object({
  type: z.enum(["offer", "request"]),
  frequency: z.enum(["one_time", "daily", "weekly"]),
  origin: z.string().min(1),
  originLocation: z.object({ lat: z.number(), lng: z.number() }),
  destination: z.string().min(1),
  destinationLocation: z.object({ lat: z.number(), lng: z.number() }),
  date: z.string().min(1),
  time: z.string().min(1),
  timeFlexibility: z.string(),
  availableSeats: z.number().int().min(0),
  suggestedContribution: z.string().regex(/^\d+(\.\d{1,2})?$/).optional(),
  luggageInfo: z.string().max(1000).optional(),
  driverNotes: z.string().max(1000).optional(),
});
export type CreateRideInput = z.infer<typeof createRideSchema>;

export const createPostSchema = z.object({
  communityId: z.string().min(1),
  title: z.string().max(160).optional(),
  body: z.string().min(1).max(8000),
  tags: z.array(z.string()).optional(),
});
export type CreatePostInput = z.infer<typeof createPostSchema>;

export const createExpenseSchema = z.object({
  title: z.string().min(1).max(160),
  description: z.string().max(1000).optional(),
  totalAmount: z.string().regex(/^\d+(\.\d{1,2})?$/, "Enter a valid amount"),
  currency: z.string().default("USD"),
  paidByIds: z.array(z.string()).min(1),
  splitStrategy: z.enum(["equal", "exact", "percentage", "shares"]),
  splits: z.array(z.object({
    userId: z.string(),
    amount: z.string().optional(),
    percent: z.number().int().min(0).max(100).optional(),
    shares: z.number().int().positive().optional(),
  })).min(1),
  date: z.string().min(1),
  category: z.string().max(60).optional(),
});
export type CreateExpenseInput = z.infer<typeof createExpenseSchema>;

export const createEventSchema = z.object({
  title: z.string().min(3).max(160),
  description: z.string().min(10).max(4000),
  venueType: z.enum(["online", "in_person"]),
  location: z.string().max(200).optional(),
  onlineUrl: z.string().url().optional(),
  city: z.string().max(80).optional(),
  state: z.string().length(2).optional(),
  startTime: z.string().min(1),
  endTime: z.string().min(1).optional(),
  capacity: z.number().int().positive().optional(),
});
export type CreateEventInput = z.infer<typeof createEventSchema>;

export const reportSchema = z.object({
  targetType: z.enum(["room", "ride", "post", "comment", "message", "job", "event", "user", "resource"]),
  targetId: z.string().min(1),
  reason: z.enum(["spam", "scam", "harassment", "hate", "inappropriate", "misinformation", "safety", "other"]),
  detail: z.string().max(2000).optional(),
});
export type ReportInput = z.infer<typeof reportSchema>;
