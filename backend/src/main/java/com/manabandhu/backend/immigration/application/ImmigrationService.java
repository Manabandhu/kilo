package com.manabandhu.backend.immigration.application;

import com.manabandhu.backend.common.api.PagedResult;
import com.manabandhu.backend.common.exception.BusinessException;
import com.manabandhu.backend.immigration.api.ImmigrationApi;
import com.manabandhu.backend.immigration.api.ImmigrationApi.CreateQuestionRequest;
import com.manabandhu.backend.immigration.api.ImmigrationApi.CreateResourceRequest;
import com.manabandhu.backend.immigration.api.ImmigrationApi.QuestionResponse;
import com.manabandhu.backend.immigration.api.ImmigrationApi.ResourceResponse;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationCategoryEntity;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationCategoryRepository;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationQuestionEntity;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationQuestionRepository;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationResourceEntity;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationResourceEntity.ResourceStatus;
import com.manabandhu.backend.immigration.infrastructure.ImmigrationResourceRepository;
import com.manabandhu.backend.security.Authorization;
import com.manabandhu.backend.security.CurrentUser;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImmigrationService {

    private final ImmigrationCategoryRepository categoryRepository;
    private final ImmigrationResourceRepository resourceRepository;
    private final ImmigrationQuestionRepository questionRepository;

    public ImmigrationService(ImmigrationCategoryRepository categoryRepository,
                              ImmigrationResourceRepository resourceRepository,
                              ImmigrationQuestionRepository questionRepository) {
        this.categoryRepository = categoryRepository;
        this.resourceRepository = resourceRepository;
        this.questionRepository = questionRepository;
    }

    public List<ImmigrationApi.CategoryResponse> categories() {
        return categoryRepository.findAll().stream().map(ImmigrationApi::toResponse).toList();
    }

    public PagedResult<ResourceResponse> resources(UUID categoryId, String type, String query, int page, int size) {
        Page<ImmigrationResourceEntity> p = resourceRepository.search(categoryId, type, query, PageRequest.of(page, size));
        return PagedResult.of(p.getContent().stream().map(ImmigrationApi::toResponse).toList(), page, size, p.getTotalElements());
    }

    public ResourceResponse resource(UUID id) {
        return ImmigrationApi.toResponse(require(id));
    }

    @Transactional
    public ResourceResponse create(CurrentUser user, CreateResourceRequest req) {
        Authorization.requireStaff(user);
        ImmigrationResourceEntity e = new ImmigrationResourceEntity();
        e.setCategoryId(req.categoryId());
        e.setType(req.type());
        e.setTitle(req.title());
        e.setBody(req.body());
        e.setTags(req.tags() == null ? new String[0] : req.tags());
        e.setSourceReferences(req.sourceReferences() == null ? new String[0] : req.sourceReferences());
        e.setVersionDate(LocalDate.now());
        e.setLastReviewedAt(Instant.now());
        e.setTrustedContributor(true);
        e.setAuthorId(user.userId());
        e.setStatus(ResourceStatus.published);
        return ImmigrationApi.toResponse(resourceRepository.save(e));
    }

    @Transactional
    public void helpful(UUID id) {
        ImmigrationResourceEntity e = require(id);
        e.setHelpfulCount(e.getHelpfulCount() + 1);
        resourceRepository.save(e);
    }

    @Transactional
    public QuestionResponse ask(CurrentUser user, CreateQuestionRequest req) {
        ImmigrationQuestionEntity q = new ImmigrationQuestionEntity();
        q.setResourceId(req.resourceId());
        q.setBody(req.body());
        q.setStatus(ImmigrationQuestionEntity.QuestionStatus.open);
        return ImmigrationApi.toResponse(questionRepository.save(q));
    }

    @Transactional
    public QuestionResponse answer(CurrentUser user, UUID questionId, String answer) {
        Authorization.requireStaff(user);
        ImmigrationQuestionEntity q = questionRepository.findById(questionId)
                .orElseThrow(() -> BusinessException.notFound("Question not found"));
        q.setAnswer(answer);
        q.setAnsweredById(user.userId());
        q.setStatus(ImmigrationQuestionEntity.QuestionStatus.answered);
        return ImmigrationApi.toResponse(questionRepository.save(q));
    }

    private ImmigrationResourceEntity require(UUID id) {
        return resourceRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Resource not found"));
    }
}
