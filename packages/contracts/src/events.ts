import type { UUID, ISODateTime, ISODate, UsState, Coordinates } from "./common";
import type { PublicProfileSummary } from "./rooms";

export type EventStatus = "scheduled" | "cancelled" | "completed";
export type EventVenue = "online" | "in_person";
export type RsvpStatus = "going" | "interested" | "waitlist" | "declined";

export interface AppEvent {
  id: UUID;
  title: string;
  description: string;
  venueType: EventVenue;
  location?: string;
  locationPoint?: Coordinates;
  onlineUrl?: string;
  city?: string;
  state?: UsState;
  startTime: ISODateTime;
  endTime?: ISODateTime;
  capacity?: number;
  attendeeCount: number;
  waitlistCount: number;
  organizerId: UUID;
  organizer?: PublicProfileSummary;
  coverImageUrl?: string;
  status: EventStatus;
  saved: boolean;
  userRsvp?: RsvpStatus;
  createdAt: ISODateTime;
}

export interface CreateEventRequest {
  title: string;
  description: string;
  venueType: EventVenue;
  location?: string;
  locationPoint?: Coordinates;
  onlineUrl?: string;
  city?: string;
  state?: UsState;
  startTime: ISODateTime;
  endTime?: ISODateTime;
  capacity?: number;
  coverImageUrl?: string;
}

export interface Rsvp {
  eventId: UUID;
  userId: UUID;
  status: RsvpStatus;
  createdAt: ISODateTime;
}
