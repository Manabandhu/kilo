import type { UUID, ISODateTime, ISODate } from "./common";

export interface ImmigrationCategory {
  id: UUID;
  slug: string;
  name: string;
  description: string;
  orderIndex: number;
}

export type ResourceType = "guide" | "checklist" | "faq";

export interface ImmigrationResource {
  id: UUID;
  categoryId: UUID;
  type: ResourceType;
  title: string;
  body: string;
  tags: string[];
  versionDate: ISODate;
  lastReviewedAt: ISODateTime;
  sourceReferences: string[];
  trustedContributor: boolean;
  authorId?: UUID;
  saved: boolean;
  helpfulCount: number;
  userHelpful?: boolean;
}

export interface CreateResourceRequest {
  categoryId: UUID;
  type: ResourceType;
  title: string;
  body: string;
  tags?: string[];
  sourceReferences?: string[];
}

export interface CommunityQuestion {
  id: UUID;
  resourceId?: UUID;
  authorId: UUID;
  body: string;
  answer?: string;
  answeredById?: UUID;
  status: "open" | "answered" | "flagged";
  createdAt: ISODateTime;
  updatedAt: ISODateTime;
}

export interface CreateQuestionRequest {
  resourceId?: UUID;
  body: string;
}
