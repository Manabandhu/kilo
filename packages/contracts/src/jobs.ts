import type { UUID, ISODateTime, ISODate, UsState, Coordinates } from "./common";
import type { PublicProfileSummary } from "./rooms";

export type JobType = "full_time" | "part_time" | "contract" | "internship" | "gig";
export type WorkMode = "remote" | "hybrid" | "onsite";
export type ExperienceLevel = "entry" | "mid" | "senior" | "lead" | "any";
export type JobSource = "community" | "company" | "feed" | "admin";

export interface Job {
  id: UUID;
  title: string;
  company: string;
  location: string;
  state?: UsState;
  locationPoint?: Coordinates;
  workMode: WorkMode;
  employmentType: JobType;
  experienceLevel: ExperienceLevel;
  salaryMin?: number;
  salaryMax?: number;
  currency: string;
  skills: string[];
  sponsorshipOffered?: boolean;
  source: JobSource;
  applicationUrl?: string;
  postedById?: UUID;
  poster?: PublicProfileSummary;
  expiresAt?: ISODate;
  postedAt: ISODateTime;
  saved: boolean;
}

export interface JobFilter {
  query?: string;
  workMode?: WorkMode;
  employmentType?: JobType;
  experienceLevel?: ExperienceLevel;
  state?: UsState;
  sponsorshipOffered?: boolean;
  minSalary?: number;
}

export interface CreateJobRequest {
  title: string;
  company: string;
  location: string;
  state?: UsState;
  locationPoint?: Coordinates;
  workMode: WorkMode;
  employmentType: JobType;
  experienceLevel: ExperienceLevel;
  salaryMin?: number;
  salaryMax?: number;
  currency?: string;
  skills: string[];
  sponsorshipOffered?: boolean;
  applicationUrl?: string;
  expiresAt?: ISODate;
}

export type ReferralKind = "request" | "offer";

export interface Referral {
  id: UUID;
  jobId?: UUID;
  kind: ReferralKind;
  fromUserId: UUID;
  toUserId?: UUID;
  company?: string;
  note?: string;
  status: "open" | "connected" | "closed";
  createdAt: ISODateTime;
}
