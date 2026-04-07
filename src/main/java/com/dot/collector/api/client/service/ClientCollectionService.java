package com.dot.collector.api.client.service;

import com.dot.collector.api.client.dto.ClientCollectionDTO;
import com.dot.collector.api.client.dto.ClientCollectionLiteDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientCollectionService {

    public List<ClientCollectionLiteDTO> getUserCollections() {
        return new ArrayList<>();
    }

    public ClientCollectionDTO getCollectionById(Long id) {
        return null;
    }

    public ClientCollectionDTO cloneCollection(Long sourceCollectionId) {
        return null;
    }
}
