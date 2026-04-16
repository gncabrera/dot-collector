package com.nookx.api.service.upload;

import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AssetUploadLinkHandlerRegistry {

    private static final String ENTITY_NAME = "megaAsset";

    private final Map<AttachmentType, AssetUploadLinkHandler> handlers = new EnumMap<>(AttachmentType.class);

    public AssetUploadLinkHandlerRegistry(List<AssetUploadLinkHandler> handlerList) {
        for (AssetUploadLinkHandler handler : handlerList) {
            handlers.put(handler.getAttachmentType(), handler);
        }
        if (handlers.size() != AttachmentType.values().length) {
            throw new IllegalStateException("Missing AssetUploadLinkHandler for some AttachmentType values");
        }
    }

    public AssetUploadLinkHandler getHandler(AttachmentType type) {
        AssetUploadLinkHandler handler = handlers.get(type);
        if (handler == null) {
            throw new BadRequestAlertException("Unsupported attachment type", ENTITY_NAME, "unsupportedattachmenttype");
        }
        return handler;
    }
}
