import type { UUID, ISODateTime, ISODate, UsState, Coordinates } from "./common";

export type RoomStatus = "active" | "paused" | "closed" | "deleted";
export type RoomType = "private" | "shared" | "studio" | "1br" | "2br" | "3br+" | "sublet" | "homestay";
export type PropertyType = "apartment" | "house" | "condo" | "townhouse" | "dorm" | "basement";
export type FurnishedStatus = "furnished" | "partial" | "unfurnished";
export type GenderPreference = "any" | "female" | "male" | "no_preference";
export type PetPolicy = "allowed" | "cats" | "dogs" | "none";
export type SmokingPolicy = "no_smoking" | "outdoor_only" | "allowed";
export type ContactPreference = "in_app" | "email" | "phone";

export interface RoomListing {
  id: UUID;
  title: string;
  description: string;
  monthlyRent: number;
  securityDeposit?: number;
  city: string;
  state: UsState;
  zip?: string;
  approximateLocation?: Coordinates;
  exactAddressShared: boolean;
  roomType: RoomType;
  propertyType: PropertyType;
  furnished: FurnishedStatus;
  availableDate?: ISODate;
  leaseDurationMonths?: number;
  utilitiesIncluded: boolean;
  amenities: string[];
  parking: boolean;
  petPolicy: PetPolicy;
  smokingPolicy: SmokingPolicy;
  occupancy: number;
  genderPreference: GenderPreference;
  contactPreference: ContactPreference;
  images: string[];
  status: RoomStatus;
  posterId: UUID;
  poster?: PublicProfileSummary;
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface PublicProfileSummary {
  id: UUID;
  displayName: string;
  avatarUrl?: string;
  ratingAverage: number;
  verification: { emailVerified: boolean; phoneVerified: boolean; profileCompleted: boolean };
}

export interface RoomFilter {
  city?: string;
  state?: UsState;
  minRent?: number;
  maxRent?: number;
  roomType?: RoomType;
  propertyType?: PropertyType;
  furnished?: FurnishedStatus;
  utilitiesIncluded?: boolean;
  parking?: boolean;
  petPolicy?: PetPolicy;
  smokingPolicy?: SmokingPolicy;
  genderPreference?: GenderPreference;
  availableFrom?: ISODate;
  query?: string;
}

export interface CreateRoomRequest {
  title: string;
  description: string;
  monthlyRent: number;
  securityDeposit?: number;
  city: string;
  state: UsState;
  zip?: string;
  approximateLocation?: Coordinates;
  exactAddressShared?: boolean;
  roomType: RoomType;
  propertyType: PropertyType;
  furnished: FurnishedStatus;
  availableDate?: ISODate;
  leaseDurationMonths?: number;
  utilitiesIncluded: boolean;
  amenities: string[];
  parking: boolean;
  petPolicy: PetPolicy;
  smokingPolicy: SmokingPolicy;
  occupancy: number;
  genderPreference: GenderPreference;
  contactPreference: ContactPreference;
  images: string[];
}

export type UpdateRoomRequest = Partial<CreateRoomRequest> & { status?: RoomStatus };
