package com.transport.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transport.management.dto.response.AssignmentHistoryDto;
import com.transport.management.repository.AssignmentHistoryRepository;

@Service
public class HistoryService {

    @Autowired
    private AssignmentHistoryRepository historyRepository;

    public List<AssignmentHistoryDto> getAssignmentHistoryForOwner(String ownerId) {
        return historyRepository.findHistoryByOwnerId(ownerId);
    }
}

