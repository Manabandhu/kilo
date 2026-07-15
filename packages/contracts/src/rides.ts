import type { UUID, ISODateTime, ISODate, UsState, Coordinates } from "./common";
import type { PublicProfileSummary } from "./rooms";

export type RideStatus = "draft" | "published" | "full" | "in_progress" | "completed" | "cancelled";
export type RideType = "offer" | "request";
export type RideFrequency = "one_time" | "daily" | "weekly";
export type TimeFlexibility = "exact" | "plus_minus_15" | "plus_minus_30" | "plus_minus_60" | "flexible";
export type SeatRequestStatus = "pending" | "approved" | "rejected" | "cancelled";
export type RideRole = "driver" | "rider";

export interface RideStop {
  name: string;
  location: Coordinates;
}

export interface Ride {
  id: UUID;
  type: RideType;
  frequency: RideFrequency;
  origin: string;
  originLocation: Coordinates;
  destination: string;
  destinationLocation: Coordinates;
  stops: RideStop[];
  date: ISODate;
  time: string; // HH:mm
  timeFlexibility: TimeFlexibility;
  availableSeats: number;
  suggestedContribution?: number;
  luggageInfo?: string;
  driverNotes?: string;
  status: RideStatus;
  driverId: UUID;
  driver?: PublicProfileSummary;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}
